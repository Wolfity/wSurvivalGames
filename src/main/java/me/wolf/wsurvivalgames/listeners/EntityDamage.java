package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    private final SurvivalGamesPlugin plugin;

    public EntityDamage(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLobbyDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(plugin.getSgPlayers().get(event.getEntity().getUniqueId()))) {
                if (arena.getArenaState() != ArenaState.INGAME) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
