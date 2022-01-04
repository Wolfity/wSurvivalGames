package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class BlockBreak implements Listener {

    private final SurvivalGamesPlugin plugin;

    public BlockBreak(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        event.setCancelled(plugin.getPlayerManager().getSGPlayer(event.getPlayer().getUniqueId()) != null);

    }

}
