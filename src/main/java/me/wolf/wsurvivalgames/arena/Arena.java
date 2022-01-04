package me.wolf.wsurvivalgames.arena;

import lombok.Getter;
import lombok.Setter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.CustomLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter

public class Arena {

    private final SurvivalGamesPlugin plugin;

    private final String name;
    private final List<CustomLocation> spawnLocations;
    private final Set<SGPlayer> arenaMembers;
    private final HashMap<Material, Integer> tier1ChestContents;
    private final HashMap<Material, Integer> tier2ChestContents;
    private final Set<Location> openedChests;
    @Setter
    private ArenaState arenaState;
    @Setter
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    @Setter
    private int lobbyCountdown, gameTimer, cageTimer, graceTimer, minPlayer, maxPlayers, chestRefill;


    private File arenaConfigFile;

    protected Arena(final String name, final SurvivalGamesPlugin plugin, final int gameTimer, final int lobbyCountdown, final int cageTimer,
                    final int graceTimer, final int minPlayer, final int maxPlayers, final int chestRefill) {
        this.plugin = plugin;
        this.name = name;
        this.gameTimer = gameTimer;
        this.lobbyCountdown = lobbyCountdown;
        this.cageTimer = cageTimer;
        this.graceTimer = graceTimer;
        this.minPlayer = minPlayer;
        this.maxPlayers = maxPlayers;
        this.chestRefill = chestRefill;
        this.spawnLocations = new ArrayList<>();
        this.tier1ChestContents = new HashMap<>();
        this.tier2ChestContents = new HashMap<>();
        this.openedChests = new HashSet<>();
        this.arenaMembers = new HashSet<>();
        this.arenaState = ArenaState.READY;
    }


    public void createConfig() {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", name.toLowerCase() + ".yml");
        arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
            arenaConfig.save(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException ignore) {

        }
        if (!arenaConfigFile.exists()) {
            arenaConfigFile.getParentFile().mkdirs();
            try {
                arenaConfigFile.createNewFile();
                arenaConfig.load(arenaConfigFile);
                arenaConfig.set("min-players", 2);
                arenaConfig.set("max-players", 5);
                arenaConfig.set("cage-timer", 10);
                arenaConfig.set("lobby-countdown", 10);
                arenaConfig.set("game-timer", 600);
                arenaConfig.set("grace-timer", 30);
                arenaConfig.set("chest-refill", 60);
                arenaConfig.save(arenaConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public void addSpawnLocation(final CustomLocation customLocation) {
        if (!spawnLocations.contains(customLocation)) {
            spawnLocations.add(customLocation);
        }
    }

    public void decrementGameTimer() {
        gameTimer--;
    }

    public void decrementLobbyCountdown() {
        lobbyCountdown--;
    }

    public void decrementCageTimer() {
        cageTimer--;
    }

    public void decrementGraceTimer() {
        graceTimer--;
    }

    public void decrementChestRefillTimer() {
        chestRefill--;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}