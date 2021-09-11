package me.wolf.wsurvivalgames.arena;

import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
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
        final Arena arena = new Arena(arenaName, 600, 10, 10, 30, 1, 5, 120, plugin);

        arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
        arena.setLobbyCountdown(arena.getArenaConfig().getInt("lobby-countdown"));
        arena.setChestRefill(arena.getArenaConfig().getInt("chest-refill"));
        arena.setCageTimer(arena.getArenaConfig().getInt("cage-timer"));
        arena.setGraceTimer(arena.getArenaConfig().getInt("grace-timer"));


        plugin.getArenas().add(arena);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arenaName));
        arenaWorld.setAutoSave(false);
        plugin.getArenas().add(arena);
        return arena;
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

            arena.setWaitingRoomLoc(CustomLocation.deserialize(arena.getArenaConfig().getString("LobbySpawn")));

            for (final String key : arena.getArenaConfig().getConfigurationSection("spawn-locations").getKeys(false)) {
                arena.addSpawnLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("spawn-locations." + key)));
            }

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());

        }

    }

    public void saveArenas() {
        for (final Arena arena : plugin.getArenas()) {
            arena.saveArena(arena.getName());
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

