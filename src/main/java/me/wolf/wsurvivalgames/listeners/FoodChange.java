package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.player.SGPlayer;
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
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getEntity().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;
        event.setCancelled(arena.getArenaState() != ArenaState.INGAME);
        // cancel if the arenastate is not ingame (At this point we know the player isn't null

    }

}
