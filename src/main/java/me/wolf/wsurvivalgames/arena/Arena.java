package me.wolf.wsurvivalgames.arena;

import com.cryptomorin.xseries.XMaterial;
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

@SuppressWarnings("ConstantConditions")
@Getter
public class Arena {

    private final SurvivalGamesPlugin plugin;

    private final String name;
    @Setter
    private ArenaState arenaState = ArenaState.READY;
    @Setter
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    private final List<CustomLocation> spawnLocations = new ArrayList<>();
    private final Set<SGPlayer> arenaMembers = new HashSet<>();
    private final HashMap<Material, Integer> tier1ChestContents = new HashMap<>();
    private final HashMap<Material, Integer> tier2ChestContents = new HashMap<>();
    private final Set<Location> openedChests;

    @Setter
    private int lobbyCountdown, gameTimer, cageTimer, graceTimer, minPlayer, maxPlayers, chestRefill;


    public File arenaConfigFile;

    protected Arena(final String name, final int gameTimer, final int lobbyCountdown, final int cageTimer, final int graceTimer, final int minPlayer, final int maxPlayers, final int chestRefill, final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        createConfig(name);
        this.gameTimer = gameTimer;
        this.lobbyCountdown = lobbyCountdown;
        this.cageTimer = cageTimer;
        this.graceTimer = graceTimer;
        this.minPlayer = minPlayer;
        this.maxPlayers = maxPlayers;
        this.chestRefill = chestRefill;
        this.openedChests = new HashSet<>();

    }



    public void saveArena(final String arenaName) {
        arenaConfig.set("LobbySpawn", waitingRoomLoc.serialize());

        int i = 1;
        for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
            arenaConfig.set("spawn-locations." + i, location.serialize());
            i++;
        }

        try {
            arenaConfig.save(arenaConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfig(final String cfgName) {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", cfgName.toLowerCase() + ".yml");
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
                arenaConfig.set("min-players", 1);
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

    public void resetGameTimer() {
        this.gameTimer = arenaConfig.getInt("game-timer") + 1;
    }

    public void resetLobbyCountdownTimer() {
        this.lobbyCountdown = arenaConfig.getInt("lobby-countdown") + 1;
    }

    public void decrementCageTimer() {
        cageTimer--;
    }

    public void resetCageTimer() {
        this.cageTimer = arenaConfig.getInt("cage-timer");
    }

    public void decrementGraceTimer() {
        graceTimer--;
    }

    public void resetGraceTimer() {
        this.graceTimer = arenaConfig.getInt("grace-timer");
    }

    public void decrementChestRefillTimer() {
        chestRefill--;
    }

    public void resetChestRefillTimer() {
        this.chestRefill = arenaConfig.getInt("chest-refill");
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