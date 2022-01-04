package me.wolf.wsurvivalgames.killeffect;

import lombok.Data;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

@Data
public class KillEffect {

    final PotionEffect potionEffect;
    private final String name;
    private final Material icon;
    private final boolean isEnabled;
    @Setter
    private boolean isActive;

    public KillEffect(final String name, final Material icon, final boolean isEnabled, final PotionEffect potionEffect) {
        this.name = name;
        this.icon = icon;
        this.isEnabled = isEnabled;
        this.isActive = false;
        this.potionEffect = potionEffect;
    }
}
