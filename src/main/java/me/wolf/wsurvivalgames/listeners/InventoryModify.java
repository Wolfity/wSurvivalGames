package me.wolf.wsurvivalgames.listeners;

import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.player.SGPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryModify implements Listener {

    private final SurvivalGamesPlugin plugin;

    public InventoryModify(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getPlayer().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;

        event.setCancelled(arena.getArenaState() == ArenaState.COUNTDOWN || arena.getArenaState() == ArenaState.GAMESPAWN || arena.getArenaState() == ArenaState.READY);
        // cancel if the arena state is either countdown, gamespawn or ready. (Players can only drop items ingame)
    }

    // not allowing players to move items in their inventory while in the following arena states
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getWhoClicked().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;

        if (arena.getArenaState() == ArenaState.COUNTDOWN || arena.getArenaState() == ArenaState.GAMESPAWN || arena.getArenaState() == ArenaState.READY) {
            final List<ItemStack> items = new ArrayList<>();
            items.add(event.getCurrentItem());
            items.add(event.getCursor());
            items.add((event.getClick() == ClickType.NUMBER_KEY) ?
                    event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());
            for (final ItemStack item : items) {
                if (item != null)
                    event.setCancelled(true);
            }
        }
    }
}
