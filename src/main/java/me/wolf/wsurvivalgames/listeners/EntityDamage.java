package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.player.SGPlayer;
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
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getEntity().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;

        event.setCancelled(arena.getArenaState() != ArenaState.INGAME);

    }
}
