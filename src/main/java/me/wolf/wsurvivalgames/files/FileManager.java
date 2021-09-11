package me.wolf.wsurvivalgames.files;


import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.utils.Utils;

public class FileManager {

    @Getter
    public YamlConfig kitsConfig, lootConfig, killeffectsConfig;

    public FileManager(final SurvivalGamesPlugin plugin) {
        try {
            kitsConfig = new YamlConfig("kits.yml", plugin);
            lootConfig = new YamlConfig("loot.yml", plugin);
            killeffectsConfig = new YamlConfig("killeffects.yml", plugin);
        } catch (final Exception e) {
            System.out.println(Utils.colorize("&4Something went wrong while loading the yml files"));
        }

    }

    public void reloadConfigs() {
        kitsConfig.reloadConfig();
        lootConfig.reloadConfig();
        killeffectsConfig.reloadConfig();
    }

}

