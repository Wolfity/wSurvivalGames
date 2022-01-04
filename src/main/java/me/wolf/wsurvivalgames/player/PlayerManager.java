package me.wolf.wsurvivalgames.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, SGPlayer> sgPlayers = new HashMap<>();


    public void createSGPlayer(final UUID uuid) {
        this.sgPlayers.put(uuid, new SGPlayer(uuid));
    }

    // throws NPE if the SGPlayer doesn't exist
    public SGPlayer getSGPlayer(final UUID uuid) {
        return sgPlayers.get(uuid);
    }

    public void removeSGPlayer(final UUID uuid) {
        this.sgPlayers.remove(uuid);
    }

}
