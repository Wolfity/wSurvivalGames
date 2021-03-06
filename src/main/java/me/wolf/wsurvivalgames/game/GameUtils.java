package me.wolf.wsurvivalgames.game;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.killeffect.KillEffect;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GameUtils {

    private final SurvivalGamesPlugin plugin;

    public GameUtils(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    public void giveLobbyInventory(final Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setSaturation(20);

        player.getInventory().setItem(3, Utils.createItem(XMaterial.WOODEN_SWORD.parseMaterial(), "&bClick To Select a Kit", 1));
        player.getInventory().setItem(4, Utils.createItem(XMaterial.IRON_SWORD.parseMaterial(), "&bClick To Select a Kill Effect", 1));
    }

    public void openKitSelector(final SGPlayer player) {
        final Inventory menu = Bukkit.createInventory(null, 27, Utils.colorize("&bKit Selector"));

        plugin.getKitManager().getKits().forEach(kit -> {
            final ItemStack icon = Utils.createItem(kit.getIcon(), Utils.colorize(kit.getDisplay()), 1);
            menu.addItem(icon);
        });
        player.getBukkitPlayer().openInventory(menu);

    }

    public void openKillEffectSelector(final SGPlayer player) {
        final Inventory menu = Bukkit.createInventory(null, 27, Utils.colorize("&bKill Effect Selector"));
        plugin.getKillEffectManager().getKillEffectSet().stream().filter(KillEffect::isEnabled).forEach(killEffect -> {
            final ItemStack icon = Utils.createItem(killEffect.getIcon(), Utils.colorize(killEffect.getName()), 1);
            menu.addItem(icon);
        });

        player.getBukkitPlayer().openInventory(menu);

    }

    public void giveGameInventory(final SGPlayer player) {
        player.getBukkitPlayer().setSaturation(20);
        if (player.getKit() == null) return;

        player.getKit().getKitItems().forEach(item -> {
            player.getInventory().addItem(item);
        });


    }

}
