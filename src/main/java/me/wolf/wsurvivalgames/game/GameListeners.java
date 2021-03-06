package me.wolf.wsurvivalgames.game;

import com.cryptomorin.xseries.XMaterial;
import me.wolf.wsurvivalgames.SurvivalGamesPlugin;
import me.wolf.wsurvivalgames.arena.Arena;
import me.wolf.wsurvivalgames.arena.ArenaState;
import me.wolf.wsurvivalgames.constants.Constants;
import me.wolf.wsurvivalgames.player.SGPlayer;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class GameListeners implements Listener {

    private final SurvivalGamesPlugin plugin;

    public GameListeners(final SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    // this listener deals with if a player kills another player
    @EventHandler
    public void onKill(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) return;
        final SGPlayer sgKilled = plugin.getPlayerManager().getSGPlayer(event.getEntity().getUniqueId());
        final SGPlayer sgKiller = plugin.getPlayerManager().getSGPlayer(event.getDamager().getUniqueId());

        if (sgKilled == null || sgKiller == null) return;


        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgKiller);
        if (arena == null) return;

        if (event.getDamage() >= sgKilled.getHealth()) {
            setSpectator(sgKilled);
            sendKillMessage(sgKiller, sgKilled, arena);
            sgKiller.incrementKills();
            applyKillEffect(sgKiller.getBukkitPlayer());

            if (isLastAlive(arena)) {
                plugin.getGameManager().setGameState(GameState.END, arena);
            }

        }
    }

    // if a player dies by fall damage or any other natural deaths, set them to spectator as well
    @EventHandler
    public void onNaturalDeath(final PlayerDeathEvent event) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getEntity().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;
        setSpectator(sgPlayer);

        if (isLastAlive(arena)) {
            plugin.getGameManager().setGameState(GameState.END, arena);
        }
    }


    // method for interacting with chests, if it hasn't been opened, add loot to it, and add it to a Set
    @EventHandler
    public void onChestOpen(final PlayerInteractEvent event) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getPlayer().getUniqueId());
        final Action action = event.getAction();
        if (event.getClickedBlock() == null) return;
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;


        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!(event.getClickedBlock().getState() instanceof Chest)) return;
            if (arena.getArenaState() == ArenaState.INGAME || arena.getArenaState() == ArenaState.GRACE) {
                final Chest chest = (Chest) event.getClickedBlock().getState();

                if (!arena.getOpenedChests().contains(chest.getLocation())) {
                    arena.getOpenedChests().add(chest.getLocation());

                    final Inventory chestInventory = (chest).getInventory();
                    final Map<Material, Integer> tier1chestContents = plugin.getArenaManager().getArena(arena.getName()).getTier1ChestContents();
                    final List<Material> tier1ChestMapKeys = new ArrayList<>(tier1chestContents.keySet());

                    final Map<Material, Integer> tier2chestContents = plugin.getArenaManager().getArena(arena.getName()).getTier2ChestContents();
                    final List<Material> tier2ChestMapKeys = new ArrayList<>(tier2chestContents.keySet());


                    if (Utils.calculateChance(plugin.getConfig().getDouble("tier-2-chest-chance"))) {
                        fillChests(tier2ChestMapKeys, tier2chestContents, chestInventory);
                    } else {
                        fillChests(tier1ChestMapKeys, tier1chestContents, chestInventory);
                    }
                }
            }
        }
    }

    // check if the player is in their spawn position, so they aren't able to move, until the game officially starts
    @EventHandler
    public void onSpawnPosState(final PlayerMoveEvent event) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getPlayer().getUniqueId());
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;

        if (arena.getArenaState() == ArenaState.GAMESPAWN) {
            event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ()));

        }
    }

    // The menus from kits and kill effects
    @EventHandler
    public void selectorMenu(final PlayerInteractEvent event) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getPlayer().getUniqueId());
        final Action action = event.getAction();
        if (sgPlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(sgPlayer);
        if (arena == null) return;

        if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.GAMESPAWN || arena.getArenaState() == ArenaState.COUNTDOWN) {
            if (event.getPlayer().getItemInHand().getType() == XMaterial.WOODEN_SWORD.parseMaterial()) {
                if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                    plugin.getGameUtils().openKitSelector(sgPlayer);
                }
            } else if (event.getPlayer().getItemInHand().getType() == XMaterial.IRON_SWORD.parseMaterial()) {
                if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                    plugin.getGameUtils().openKillEffectSelector(sgPlayer);
                }
            }

        }
    }

    // the listener to check which kit/kill effect a player selects
    @EventHandler
    public void onMenuSelect(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(event.getWhoClicked().getUniqueId());
        if (sgPlayer == null) return;
        if (event.getView().getTitle().equalsIgnoreCase(Utils.colorize("&bKit Selector"))) {
            plugin.getKitManager().getKits().forEach(kit -> {
                if (event.getCurrentItem().getType().equals(kit.getIcon())) {
                    sgPlayer.setKit(kit);
                    sgPlayer.sendMessage(Constants.Messages.KIT_SELECTED.replace("{kit}", kit.getName()));
                }
            });

            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.colorize("&bKill Effect Selector"))) {
            plugin.getKillEffectManager().getKillEffectSet().forEach(killEffect -> {
                if (event.getCurrentItem().getType().equals(killEffect.getIcon())) {
                    sgPlayer.setKillEffect(killEffect);
                    sgPlayer.sendMessage(Constants.Messages.KILLEFFECT_SELECTED.replace("{killeffect}", killEffect.getName()));
                }
            });

            event.setCancelled(true);
        }
    }

    // dedicates a tablist to only hold the players in the same world (same arena)
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        for (final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            if (!(event.getTo().getWorld() == onlinePlayers.getWorld())) {
                player.hidePlayer(onlinePlayers);
                onlinePlayers.hidePlayer(player);
            } else {
                player.showPlayer(onlinePlayers);
                onlinePlayers.showPlayer(player);
            }
        }
    }

    // sending out the message that someone has been killed
    private void sendKillMessage(final SGPlayer killer, final SGPlayer killed, final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(sgPlayer -> sgPlayer.sendMessage(Utils.colorize("&b" + killed.getDisplayName() + " &2was killed by &b" + killer.getDisplayName())));
    }

    //setting a player to spectator mode
    private void setSpectator(final SGPlayer sgKilled) {
        final Player killed = Bukkit.getPlayer(sgKilled.getUuid());
        if (killed == null) return;

        sgKilled.setSpectator(true);
        killed.setHealth(20);
        killed.setSaturation(20);
        killed.setGameMode(GameMode.SPECTATOR);
        killed.setPlayerListName(Utils.colorize("&7[SPECTATOR] " + killed.getDisplayName()));
        killed.getInventory().clear();
        killed.getInventory().setArmorContents(null);
    }

    // applies the selected killeffect after the killer killed a player
    private void applyKillEffect(final Player killer) {
        final SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(killer.getUniqueId());
        if (sgPlayer.getKillEffect() == null) return;
        killer.addPotionEffect(sgPlayer.getKillEffect().getPotionEffect());

    }

    // checking if the last player is alive
    private boolean isLastAlive(final Arena arena) {
        return arena.getArenaMembers().stream().filter(sgPlayer -> !sgPlayer.isSpectator()).count() == 1;
    }

    // the method for filling chests. After a chest is opened, it will be put inside a Set, so players can't duplicate items by continuously opening chests.
    private void fillChests(final List<Material> chestMapKeys, final Map<Material, Integer> chestContents, final Inventory chestInventory) {
        final int itemsPerChest = ThreadLocalRandom.current().nextInt(plugin.getConfig().getInt("min-items-per-chest"),
                plugin.getConfig().getInt("max-items-per-chest"));

        for (int i = 0; i < itemsPerChest; i++) {
            final Random random = new Random();

            final int randomInt = random.nextInt(chestMapKeys.size());
            final int randomSlot = new Random().nextInt(chestInventory.getSize());
            chestInventory.setItem(randomSlot, new ItemStack(chestMapKeys.get(randomInt), chestContents.get(chestMapKeys.get(randomInt))));

        }
    }
}
