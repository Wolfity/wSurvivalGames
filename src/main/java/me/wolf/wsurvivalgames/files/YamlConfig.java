package me.wolf.wsurvivalgames.files;

import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlConfig {


    @Getter
    private final File configFile;

    @Getter
    private FileConfiguration config;

    public YamlConfig(final String configName, final SurvivalGamesPlugin plugin) throws Exception {
        configFile = new File(plugin.getDataFolder(), configName);

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(configName, false);
        }

        config = new YamlConfiguration();
        config.load(configFile);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
            reloadConfig();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
