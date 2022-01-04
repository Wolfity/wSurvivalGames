package me.wolf.wsurvivalgames.player;

import lombok.Data;
import lombok.Setter;
import me.wolf.wsurvivalgames.killeffect.KillEffect;
import me.wolf.wsurvivalgames.kits.Kit;
import me.wolf.wsurvivalgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;
import java.util.UUID;

@Data
public class SGPlayer {


    @Setter
    boolean isSpectator;
    private UUID uuid;
    private int kills;
    @Setter
    private Kit kit;
    @Setter
    private KillEffect killEffect;

    public SGPlayer(final UUID uuid) {

        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SGPlayer sgPlayer = (SGPlayer) o;
        return uuid.equals(sgPlayer.uuid);
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void sendMessage(final String s) {
        getBukkitPlayer().sendMessage(Utils.colorize(s));
    }

    public Inventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    public String getDisplayName() {
        return getBukkitPlayer().getDisplayName();
    }

    public double getHealth() {
        return getBukkitPlayer().getHealth();
    }

    public void teleport(final Location location) {
        getBukkitPlayer().teleport(location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public void incrementKills() {
        kills++;
    }


}
