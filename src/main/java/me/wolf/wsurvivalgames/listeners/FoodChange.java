package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.ArenaState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChange implements Listener {

    private final SurvivalGamesPlugin plugin;

    public FoodChange(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        plugin.getArenas().stream().filter(arena -> arena.getArenaState() != ArenaState.INGAME).forEach(arena -> {
            if (arena.getArenaMembers().contains(plugin.getSgPlayers().get(event.getEntity().getUniqueId()))) {
                event.setCancelled(true);
            }
        });
    }

}
