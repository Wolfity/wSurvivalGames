package me.wolf.wsurvivalgames.game;

import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.constants.Constants;
import me.wolf.wsurvivalgames.exception.NotEnoughSpawnsException;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.CustomLocation;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;


public class GameManager {

    private final SurvivalGamesPlugin plugin;
    @Getter
    private GameState gameState;

    public GameManager(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    public void setGameState(final GameState gameState, final Arena arena) {
        this.gameState = gameState;
        switch (gameState) {
            case RECRUITING:
                arena.setArenaState(ArenaState.READY);
                enoughPlayers(arena);
                break;
            case LOBBY_COUNTDOWN:
                arena.setArenaState(ArenaState.COUNTDOWN);
                lobbyCountdown(arena);
                break;
            case CAGES:
                arena.setArenaState(ArenaState.GAMESPAWN);
                plugin.getArenaManager().loadLoot(arena);
                teleportToSpawns(arena);
                cageTimer(arena);
                break;
            case GRACE:
                arena.setArenaState(ArenaState.GRACE);
                gracePeriodCountdown(arena);
                break;
            case ACTIVE:
                arena.setArenaState(ArenaState.INGAME);
                gameTimer(arena);
                chestRefillCheck(arena);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                sendGameEndNotification(arena);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    endGame(arena);
                    resetMap(arena);
                }, 200L);
                break;
        }
    }

    // handles the lobby countdown timer, if this ends the gamestate will be set to active and players will get notified
    private void lobbyCountdown(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.LOBBY_COUNTDOWN) {
                    this.cancel();
                }
                if (arena.getLobbyCountdown() > 0) {
                    arena.decrementLobbyCountdown();
                    arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer -> {
                        sgPlayer.sendMessage(Constants.Messages.LOBBY_COUNTDOWN.replace("{countdown}", String.valueOf(arena.getLobbyCountdown())));
                    });
                } else {
                    this.cancel();
                    arena.setLobbyCountdown(arena.getArenaConfig().getInt("lobby-countdown"));
                    arena.getArenaMembers().forEach(sgPlayer -> sgPlayer.sendMessage(Constants.Messages.GAME_WILL_START_SOON));
                    setGameState(GameState.CAGES, arena);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // the game timer, handles the time of the game + when chests refill
    private void gameTimer(final Arena arena) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.ACTIVE) {
                    this.cancel();
                }
                if (arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                    arena.getArenaMembers().
                            stream().
                            filter(Objects::nonNull).forEach(sgPlayer -> plugin.getScoreboard().gameScoreboard(sgPlayer.getBukkitPlayer(), arena));
                } else {
                    this.cancel();
                    arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
                    setGameState(GameState.END, arena);

                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // the timer that starts right after they get teleported to their spawn positions, after the countdown ends, they can move and loot
    private void cageTimer(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.CAGES) {
                    this.cancel();
                }
                if (arena.getCageTimer() > 0) {
                    arena.decrementCageTimer();
                    arena.getArenaMembers().
                            stream().
                            filter(Objects::nonNull).forEach(sgPlayer -> {
                                plugin.getScoreboard().gameScoreboard(sgPlayer.getBukkitPlayer(), arena);
                                sgPlayer.sendMessage(Constants.Messages.CAGE_COUNTDOWN.replace("{seconds}", String.valueOf(arena.getCageTimer())));
                            });
                } else {
                    this.cancel();
                    arena.getArenaMembers().forEach(sgPlayer -> {
                        final Player player = Bukkit.getPlayer(sgPlayer.getUuid());
                        sgPlayer.sendMessage(Constants.Messages.GRACE_PERIOD.replace("{seconds}", String.valueOf(arena.getGraceTimer())));

                        player.getInventory().clear();
                        player.closeInventory();
                    });
                    setGameState(GameState.GRACE, arena);
                    arena.setCageTimer(arena.getArenaConfig().getInt("cage-timer"));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // after the game has just started, there is a grace period, where players can't fight. After this ends they get their kits and can fight
    private void gracePeriodCountdown(final Arena arena) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arena.getGraceTimer() > 0) {
                    arena.decrementGraceTimer();
                } else {
                    this.cancel();
                    setGameState(GameState.ACTIVE, arena);
                    arena.getArenaMembers().forEach(sgPlayer -> {
                        sgPlayer.sendMessage(Constants.Messages.GAME_STARTED);
                        sgPlayer.sendMessage(Constants.Messages.GRACE_PERIOD_END.replace("{seconds}", String.valueOf(arena.getChestRefill())));
                        plugin.getGameUtils().giveGameInventory(sgPlayer);
                    });
                    arena.setGraceTimer(arena.getArenaConfig().getInt("grace-timer"));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // checks whenever chests will be refilled
    private void chestRefillCheck(final Arena arena) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.ACTIVE) {
                    if (arena.getChestRefill() > 0) {
                        arena.decrementChestRefillTimer();
                        if (arena.getChestRefill() == arena.getChestRefill() / 2) {
                            arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer ->
                                    sgPlayer.sendMessage(Constants.Messages.CHESTREFILL_HALFWAY.replace("{seconds}",
                                            String.valueOf(arena.getChestRefill() / 2))));
                        }
                    } else {
                        this.cancel();
                        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer -> {
                            sgPlayer.sendMessage(Constants.Messages.CHESTREFILL);
                        });
                        arena.getOpenedChests().clear();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    // handles everything in relation to ending the game, removing custom player objects, resetting their inventory, etc..
    private void endGame(final Arena arena) {

        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer -> {

            sgPlayer.getInventory().clear();
            teleportToWorld(arena);
            // clearing inventory + changing gamemode
            sgPlayer.getBukkitPlayer().getInventory().setArmorContents(null);
            sgPlayer.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);

            plugin.getPlayerManager().removeSGPlayer(sgPlayer.getUuid());

            // setting scoreboard, playerlistname and clear active effects
            sgPlayer.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            sgPlayer.getBukkitPlayer().setPlayerListName(sgPlayer.getDisplayName());
            sgPlayer.getBukkitPlayer().getActivePotionEffects().clear();

            sgPlayer.setSpectator(false);

        });
        // resetting some timers
        arena.setChestRefill(arena.getArenaConfig().getInt("chest-refill"));
        arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
        arena.getArenaMembers().clear();
    }

    // private unload the map
    private void resetMap(final Arena arena) {
        Bukkit.unloadWorld(Objects.requireNonNull(arena.getWaitingRoomLoc().toBukkitLocation().getWorld()), false);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arena.getName()));
        if (arenaWorld == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> setGameState(GameState.RECRUITING, arena), 150L); // after another 150 ticks, allow users to join (map is loaded again)

        arenaWorld.setAutoSave(false);
    }

    // check if there are enough players, if so start the lobby countdown
    private void enoughPlayers(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.RECRUITING) {
                    if (arena.getArenaMembers().size() >= arena.getMinPlayer()) {
                        setGameState(GameState.LOBBY_COUNTDOWN, arena);
                    } else this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // after the game ends, players will be teleported back to the world spawn
    private void teleportToWorld(final Arena arena) {
        arena.getArenaMembers().forEach(sgPlayer -> {
            final Location worldLoc = (Location) plugin.getConfig().get("WorldSpawn");
            sgPlayer.teleport(worldLoc);
            plugin.getPlayerManager().removeSGPlayer(sgPlayer.getUuid());
        });
    }

    /* players will be teleported to the spawn locations. Using ArrayDequeue.poll to go down the list.
       This way every player will have a different spawn spot
       If there are more players than spawn locations a NotEnoughSpawnsException will be thrown.
     */
    private void teleportToSpawns(final Arena arena) {
        final Queue<CustomLocation> remainingSpawns = new ArrayDeque<>(arena.getSpawnLocations());
        for (final SGPlayer sgPlayer : arena.getArenaMembers()) {
            if (arena.getArenaMembers().size() > arena.getSpawnLocations().size()) {
                throw new NotEnoughSpawnsException("There are more players then spawn positions!");
            }
            sgPlayer.teleport(remainingSpawns.poll().toBukkitLocation());
        }
    }

    public void teleportToLobby(final Player player, final Arena arena) {
        player.teleport(arena.getWaitingRoomLoc().toBukkitLocation());
    }

    // Adds a new player to a specific arena, creates the Custom Player object.
    public void addPlayer(final Player player, final Arena arena) {
        if (!plugin.getArenaManager().isGameActive(arena)) { // game isnt active
            if (!arena.getArenaMembers().contains(plugin.getPlayerManager().getSGPlayer(player.getUniqueId()))) { // player is not in that arena
                if (arena.getArenaMembers().isEmpty()) { // if the game is empty, set it it to recruiting
                    setGameState(GameState.RECRUITING, arena);
                }

                if (arena.getArenaMembers().size() <= arena.getMaxPlayers()) { // if the player limit hasn't been exceeded, allow to join

                    plugin.getPlayerManager().createSGPlayer(player.getUniqueId());
                    final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(player.getUniqueId());
                    arena.getArenaMembers().add(sgPlayer);

                    // set scoreboard, teleport and give the inventory
                    plugin.getScoreboard().lobbyScoreboard(player, arena);
                    teleportToLobby(player, arena);
                    plugin.getGameUtils().giveLobbyInventory(player);

                    arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(arenaMembers -> {
                        plugin.getScoreboard().lobbyScoreboard(arenaMembers.getBukkitPlayer(), arena);
                        arenaMembers.sendMessage(Constants.Messages.PLAYER_JOINED_GAME.replace("{player}", player.getDisplayName()));
                    });

                    enoughPlayers(arena);
                    sgPlayer.sendMessage(Constants.Messages.JOINED_ARENA.replace("{arena}", arena.getName()));

                } else player.sendMessage(Constants.Messages.ARENA_IS_FULL);
            } else player.sendMessage(Constants.Messages.ALREADY_IN_ARENA);
        } else player.sendMessage(Constants.Messages.GAME_IN_PROGRESS);
    }

    // remove a player from the game, teleport them, clear the custom player object
    public void removePlayer(final Player player) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(player.getUniqueId());
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (sgPlayer == null) return;
        if (arena != null) {

            arena.getArenaMembers().remove(sgPlayer);
            plugin.getPlayerManager().removeSGPlayer(player.getUniqueId());

            player.teleport((Location) Objects.requireNonNull(plugin.getConfig().get("WorldSpawn")));
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getInventory().clear();
            sgPlayer.sendMessage(Constants.Messages.LEFT_ARENA.replace("{arena}", arena.getName()));

            leaveGameCheck(arena);
            arena.getArenaMembers()
                    .stream().filter(Objects::nonNull)
                    .forEach(arenaMember -> arenaMember.sendMessage(Constants.Messages.PLAYER_LEFT_GAME.replace("{player}", player.getDisplayName())));

        } else player.sendMessage(Constants.Messages.NOT_IN_ARENA);
    }


    // If someone leaves, check if there are any players left, else reset the game
    private void leaveGameCheck(final Arena arena) {
        if (gameState == GameState.ACTIVE || gameState == GameState.GRACE) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.END, arena);
                resetTimers(arena);
            }
        } else if (gameState == GameState.LOBBY_COUNTDOWN || gameState == GameState.CAGES) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.RECRUITING, arena);
                resetTimers(arena);
            }
        }
    }

    // sends the players who are in the arena the notification that the game has endedo
    private void sendGameEndNotification(final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer -> {
            sgPlayer.sendMessage(Constants.Messages.GAME_ENDED.replace("{winner}", Utils.getWinner(arena).getDisplayName())
                    .replace("{kills}", String.valueOf(Utils.getWinner(arena).getKills())));
        });
    }

    private void resetTimers(final Arena arena) {
        arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
        arena.setCageTimer(arena.getArenaConfig().getInt("cage-timer"));
        arena.setLobbyCountdown(arena.getArenaConfig().getInt("lobby-countdown"));
        arena.setChestRefill(arena.getArenaConfig().getInt("chest-refill"));
        arena.setGraceTimer(arena.getArenaConfig().getInt("grace-timer"));
    }
}
