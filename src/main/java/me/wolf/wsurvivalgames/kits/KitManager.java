package me.wolf.wsurvivalgames.kits;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KitManager {

    private final SurvivalGamesPlugin plugin;

    public KitManager(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    private final Set<Kit> kits = new HashSet<>();


    public void loadKits() {
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
            kits.add(new Kit(kit, display, icon, materials));
        }
    }

}
