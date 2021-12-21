package me.wolf.wsurvivalgames.killeffect;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class KillEffectManager {

    private final SurvivalGamesPlugin plugin;
    public KillEffectManager(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    private final Set<KillEffect> killEffectSet = new HashSet<>();


    public void loadEffects() {
        final FileConfiguration killEffectCfg = plugin.getFileManager().getKilleffectsConfig().getConfig();
        for (final String killEffect : killEffectCfg.getConfigurationSection("kill-effects").getKeys(false)) {

            final PotionEffectType potionEffectType = PotionEffectType.getByName(killEffectCfg.getString("kill-effects." + killEffect + ".type"));
            final boolean isEnabled = killEffectCfg.getBoolean("kill-effects." + killEffect + ".enabled");
            final int duration = killEffectCfg.getInt("kill-effects." + killEffect + ".duration");
            final int amplifier = killEffectCfg.getInt("kill-effects." + killEffect + ".amplifier") - 1;
            final String iconName = killEffectCfg.getString("kill-effects." + killEffect + ".icon-name");
            final Material icon = XMaterial.valueOf(killEffectCfg.getString("kill-effects." + killEffect + ".icon")).parseMaterial();

            killEffectSet.add(new KillEffect(iconName, icon, isEnabled, new PotionEffect(potionEffectType, duration, amplifier)));

        }
    }



}
