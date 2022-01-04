package me.wolf.wsurvivalgames.commands.impl;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.commands.BaseCommand;
import me.wolf.wsurvivalgames.constants.Constants;
import me.wolf.wsurvivalgames.game.GameState;
import me.wolf.wsurvivalgames.utils.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SGCommand extends BaseCommand {

    private final SurvivalGamesPlugin plugin;

    public SGCommand(final SurvivalGamesPlugin plugin) {
        super("sg");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        if (args.length < 1 || args.length > 2) {
            tell(Constants.Messages.HELP);
        }
        if (isAdmin()) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("admin")) {
                    tell(Constants.Messages.ADMIN_HELP);
                } else if (args[0].equalsIgnoreCase("setworldspawn")) {
                    plugin.getConfig().set("WorldSpawn", player.getLocation());
                    plugin.saveConfig();
                    tell(Constants.Messages.SET_WORLD_SPAWN);
                }
            } else if (args.length == 2) {
                final String arenaName = args[1];
                if (args[0].equalsIgnoreCase("createarena")) {
                    if (Bukkit.getWorld(arenaName) == null) {
                        tell("&aCreating the new arena world...");
                        plugin.getArenaManager().createArena(arenaName);
                        player.teleport(new Location(Bukkit.getWorld(arenaName), 0, 80, 0));
                        tell(Constants.Messages.ARENA_CREATED.replace("{arena}", arenaName));
                    } else tell(Constants.Messages.ARENA_EXISTS);
                } else if (args[0].equalsIgnoreCase("deletearena")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        plugin.getArenaManager().deleteArena(arenaName);
                        tell(Constants.Messages.ARENA_DELETED);
                    } else tell(Constants.Messages.ARENA_NOT_FOUND);
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    setArenaLobby(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    setGameSpawn(player, arenaName);
                } else if (args[0].equalsIgnoreCase("forcestart")) {
                    plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        player.teleport(plugin.getArenaManager().getArena(arenaName).getWaitingRoomLoc().toBukkitLocation());
                        tell(Constants.Messages.TELEPORTED_TO_ARENA);
                    } else tell(Constants.Messages.ARENA_NOT_FOUND);
                }
            }
        }
        if (args.length == 2) {
            final String arenaName = args[1];
            if (args[0].equalsIgnoreCase("help")) {
                tell(Constants.Messages.HELP);
            } else if (args[0].equalsIgnoreCase("join")) {
                final Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena != null) {
                    if (arena.getArenaState() == ArenaState.READY) {
                        plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getArena(arenaName));
                    } else tell("&cThis game is currently not available!");
                } else tell("&cThis arena does not exist");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            plugin.getGameManager().removePlayer(player);
        }

    }

    // setting game spawns and saving them in the arena config
    private void setGameSpawn(final Player player, final String arenaName) {
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena != null) {
            if (!plugin.getArenaManager().isGameActive(arena)) {
                arena.getSpawnLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                int i = 1;
                for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
                    plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("spawn-locations." + i, location.serialize());
                    i++;
                }
                saveConfig(arena);
                tell(Constants.Messages.SET_GAME_SPAWN);
            } else {
                tell(Constants.Messages.CAN_NOT_MODIFY);
            }
        } else tell(Constants.Messages.ARENA_NOT_FOUND);
    }

    // setting an arena's waiting room lobby location
    private void setArenaLobby(final Player player, final String arenaName) {
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena != null) {
            if (!plugin.getArenaManager().isGameActive(arena)) {
                arena.getArenaConfig().set("LobbySpawn", player.getLocation().serialize());
                arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));
                tell(Constants.Messages.SET_LOBBY_SPAWN);

                saveConfig(arena);

            } else tell(Constants.Messages.CAN_NOT_MODIFY);
        } else tell(Constants.Messages.ARENA_NOT_FOUND);
    }

    private void saveConfig(final Arena arena) {
        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
