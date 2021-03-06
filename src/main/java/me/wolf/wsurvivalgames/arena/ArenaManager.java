package me.wolf.wsurvivalgames.arena;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.CustomLocation;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Objects;


@Getter
public final class ArenaManager {

    private final SurvivalGamesPlugin plugin;


    public ArenaManager(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    public Arena createArena(final String arenaName) {
        for (final Arena arena : plugin.getArenas())
            if (arena.getName().equalsIgnoreCase(arenaName))
                return getArena(arenaName);
        // Default values, can be modified in the arena config file

        final Arena arena = new Arena(arenaName, plugin, 600, 10, 10, 30, 2, 5, 60);


        arena.createConfig();


        plugin.getArenas().add(arena);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arenaName));
        arenaWorld.setAutoSave(false);

        return arena;
    }

    public void loadLoot(final Arena arena) {
        final FileConfiguration lootCfg = plugin.getFileManager().getLootConfig().getConfig();
        // loading the tier 1 and 2 chest items
        for (final String tiers : lootCfg.getConfigurationSection("chest-items").getKeys(false)) {
            for (final String key : lootCfg.getConfigurationSection("chest-items." + tiers).getKeys(false)) {

                final Material material = XMaterial.valueOf(lootCfg.getConfigurationSection("chest-items." + tiers + "." + key).getString(".item")).parseMaterial();
                final int amount = lootCfg.getConfigurationSection("chest-items." + tiers + "." + key).getInt(".amount");
                if (tiers.equalsIgnoreCase("tier-1")) {
                    arena.getTier1ChestContents().put(material, amount);
                } else if (tiers.equalsIgnoreCase("tier-2")) {
                    arena.getTier2ChestContents().put(material, amount);
                }
            }
        }
    }


    public void deleteArena(final String name) {
        final Arena arena = getArena(name);
        if (arena == null) return;

        arena.getArenaConfigFile().delete();
        plugin.getArenas().remove(arena);

        Bukkit.getWorld(name).getPlayers().stream().filter(Objects::nonNull).forEach(player -> player.teleport((Location) plugin.getConfig().get("WorldSpawn")));
        final World world = Bukkit.getWorld(name);
        Bukkit.unloadWorld(world, false);
        final File world_folder = new File(plugin.getServer().getWorldContainer() + File.separator + name + File.separator);
        deleteMap(world_folder);
    }

    // getting the arena by passing in its name
    public Arena getArena(final String name) {
        for (final Arena arena : plugin.getArenas())
            if (arena.getName().equalsIgnoreCase(name))
                return arena;

        return null;
    }

    // getting the arena a player is in by passing in the custom player object
    public Arena getArenaByPlayer(final SGPlayer sgPlayer) {
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(sgPlayer)) {
                return arena;
            }
        }
        return null;
    }

    // getting a free arena
    public Arena getFreeArena() {
        return plugin.getArenas().stream().filter(arena -> arena.getArenaState() == ArenaState.READY).findFirst().orElse(null);
    }

    // checking if a game is going on by passing in the specific arena
    public boolean isGameActive(final Arena arena) {
        return arena.getArenaState() == ArenaState.INGAME ||
                arena.getArenaState() == ArenaState.END ||
                arena.getArenaState() == ArenaState.GAMESPAWN ||
                arena.getArenaState() == ArenaState.COUNTDOWN;
    }

    public void loadArenas() {
        final File folder = new File(plugin.getDataFolder() + "/arenas");

        if (folder.listFiles() == null) {
            Bukkit.getLogger().info("&3No arenas has been found!");
            return;
        }


        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final Arena arena = createArena(file.getName().replace(".yml", ""));
            final FileConfiguration cfg = arena.getArenaConfig();
            arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(new Location(
                    Bukkit.getWorld(cfg.getString("LobbySpawn.world")),
                            cfg.getDouble("LobbySpawn.x"),
                            cfg.getDouble("LobbySpawn.y"),
                            cfg.getDouble("LobbySpawn.z"),
                            (float) cfg.getDouble("LobbySpawn.pitch"),
                            (float) cfg.getDouble("LobbySpawn.yaw"))
            ));

            for (final String key : arena.getArenaConfig().getConfigurationSection("spawn-locations").getKeys(false)) {
                arena.addSpawnLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("spawn-locations." + key)));
            }

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());

            final int minPlayers = cfg.getInt("min-players");
            final int maxPlayers = cfg.getInt("max-players");
            final int cageTimer = cfg.getInt("cage-timer");
            final int lobbyCountdown = cfg.getInt("lobby-countdown");
            final int gameTimer = cfg.getInt("game-timer");
            final int graceTimer = cfg.getInt("grace-timer");
            final int chestRefill = cfg.getInt("chest-refill");

            arena.setMinPlayer(minPlayers);
            arena.setMaxPlayers(maxPlayers);
            arena.setCageTimer(cageTimer);
            arena.setLobbyCountdown(lobbyCountdown);
            arena.setGameTimer(gameTimer);
            arena.setGraceTimer(graceTimer);
            arena.setChestRefill(chestRefill);

        }

    }


    private void deleteMap(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                this.deleteMap(file);
            }
            file.delete();
        }

        dir.delete();
    }

}

