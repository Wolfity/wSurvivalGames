package me.wolf.wsurvivalgames;

import lombok.Getter;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaManager;
import me.wolf.wsurvivalgames.commands.impl.SGCommand;
import me.wolf.wsurvivalgames.files.FileManager;
import me.wolf.wsurvivalgames.game.GameListeners;
import me.wolf.wsurvivalgames.game.GameManager;
import me.wolf.wsurvivalgames.game.GameUtils;
import me.wolf.wsurvivalgames.killeffect.KillEffectManager;
import me.wolf.wsurvivalgames.kits.KitManager;
import me.wolf.wsurvivalgames.listeners.*;
import me.wolf.wsurvivalgames.player.PlayerManager;
import me.wolf.wsurvivalgames.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class SurvivalGamesPlugin extends JavaPlugin {

    private final Set<Arena> arenas = new HashSet<>();
    @Getter
    private SurvivalGamesPlugin plugin;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private Scoreboard scoreboard;
    private GameUtils gameUtils;
    private FileManager fileManager;
    private KillEffectManager killEffectManager;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private File folder;

    @Override
    public void onEnable() {
        plugin = this;

        folder = new File(plugin.getDataFolder() + "/arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        registerCommands();
        registerListeners();
        registerManagers();

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }


    private void registerCommands() {
        Collections.singletonList(
                new SGCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new GameListeners(this),
                new PlayerQuit(this),
                new BlockBreak(this),
                new BlockPlace(this),
                new FoodChange(this),
                new EntityDamage(this),
                new InventoryModify(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.fileManager = new FileManager(this);
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        this.gameManager = new GameManager(this);
        this.scoreboard = new Scoreboard(this);
        this.gameUtils = new GameUtils(this);
        this.kitManager = new KitManager(this);
        this.killEffectManager = new KillEffectManager(this);
        this.playerManager = new PlayerManager();

        kitManager.loadKits();
        killEffectManager.loadEffects();

    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


}
