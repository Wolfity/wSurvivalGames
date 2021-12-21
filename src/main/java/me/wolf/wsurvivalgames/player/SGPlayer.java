package me.wolf.wsurvivalgames.player;

import lombok.Data;
import lombok.Setter;
import me.wolf.wsurvivalgames.killeffect.KillEffect;
import me.wolf.wsurvivalgames.kits.Kit;

import java.util.Objects;
import java.util.UUID;

@Data
public class SGPlayer {


    private UUID uuid;
    private int kills;
    @Setter
    boolean isSpectator;
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

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public void incrementKills() {
        kills++;
    }


}
