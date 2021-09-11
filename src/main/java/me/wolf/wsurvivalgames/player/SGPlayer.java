package me.wolf.wsurvivalgames.player;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import lombok.Setter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.killeffect.KillEffect;
import me.wolf.wsurvivalgames.kits.Kit;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@Data
public class SGPlayer {

    private final SurvivalGamesPlugin plugin;


    private UUID uuid;
    private int kills;
    @Setter
    boolean isSpectator;
    private final Set<Kit> kitList;
    private final Set<KillEffect> killEffectsList;

    public SGPlayer(final UUID uuid, final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;
        this.kitList = new HashSet<>();
        this.killEffectsList = new HashSet<>();

        final FileConfiguration killEffectCfg = plugin.getFileManager().getKilleffectsConfig().getConfig();
        for (final String killEffect : killEffectCfg.getConfigurationSection("kill-effects").getKeys(false)) {

            final PotionEffectType potionEffectType = PotionEffectType.getByName(killEffectCfg.getString("kill-effects." + killEffect + ".type"));
            final boolean isEnabled = killEffectCfg.getBoolean("kill-effects." + killEffect + ".enabled");
            final int duration = killEffectCfg.getInt("kill-effects." + killEffect + ".duration");
            final int amplifier = killEffectCfg.getInt("kill-effects." + killEffect + ".amplifier") - 1;
            final String iconName = killEffectCfg.getString("kill-effects." + killEffect + ".icon-name");
            final Material icon = XMaterial.valueOf(killEffectCfg.getString("kill-effects." + killEffect + ".icon")).parseMaterial();

            killEffectsList.add(new KillEffect(iconName, icon, isEnabled, new PotionEffect(potionEffectType, duration, amplifier)));

        }

        final FileConfiguration kitCfg = plugin.getFileManager().getKitsConfig().getConfig();

        for (final String kit : kitCfg.getConfigurationSection("kits").getKeys(false)) {
            final Material icon = XMaterial.valueOf(kitCfg.getString("kits." + kit + ".icon")).parseMaterial();
            final String display = kitCfg.getString(Utils.colorize("kits." + kit + ".icon-name"));
            final List<ItemStack> materials = new ArrayList<>();

            for (final String item : kitCfg.getConfigurationSection("kits." + kit + ".items").getKeys(false)) {
                final int amount = kitCfg.getConfigurationSection("kits." + kit + ".items." + item).getInt("amount");
                final String name = kitCfg.getConfigurationSection("kits." + kit + ".items." + item).getString("name");
                materials.add(Utils.createItem(
                        XMaterial.valueOf(kitCfg.getConfigurationSection("kits." + kit + ".items." + item).getString("material")).parseMaterial(),
                        name, amount));
            }
            kitList.add(new Kit(kit, display, icon, materials));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SGPlayer sgPlayer = (SGPlayer) o;
        return uuid.equals(sgPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public void incrementKills() {
        kills++;
    }


}
