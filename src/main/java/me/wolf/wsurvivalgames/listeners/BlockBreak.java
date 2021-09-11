package me.wolf.wsurvivalgames.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@SuppressWarnings("ConstantConditions")
public class BlockBreak implements Listener {

    private final SurvivalGamesPlugin plugin;

    public BlockBreak(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {

        if (event.getBlock().getType() == XMaterial.CHEST.parseMaterial()) {
            for (final Arena arena : plugin.getArenas()) {
                if (arena == null) return;
                if (!plugin.getArenaManager().isGameActive(arena)) {
                    if (event.getBlock().getWorld().getName().equalsIgnoreCase(arena.getName())) {
                        Bukkit.getWorld(arena.getName()).save();
                    }
                }
            }
        }

        if (plugin.getSgPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
