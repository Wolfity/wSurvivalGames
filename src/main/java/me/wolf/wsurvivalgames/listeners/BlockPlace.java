package me.wolf.wsurvivalgames.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.constants.Constants;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@SuppressWarnings("ConstantConditions")
public class BlockPlace implements Listener {

    private final SurvivalGamesPlugin plugin;

    public BlockPlace(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        for (final Arena arena : plugin.getArenas()) {
            if (arena == null) return;
            if (!plugin.getArenaManager().isGameActive(arena)) {
                if (event.getBlock().getType() == XMaterial.CHEST.parseMaterial()) {

                    if (event.getBlock().getWorld().getName().equalsIgnoreCase(arena.getName())) {
                        Bukkit.getWorld(arena.getName()).save();
                    }
                }
            }
            if (plugin.getArenaManager().isGameActive(arena)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Constants.Messages.CAN_NOT_MODIFY);
            }
        }

        if (plugin.getSgPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
