package me.wolf.wsurvivalgames.kits;

import lombok.Data;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Kit {

    private String name, display;
    private List<ItemStack> kitItems;
    @Setter
    private boolean isActive;
    private Material icon;

    public Kit(final String name, final String display, final Material icon, final List<ItemStack> kitItems) {
        this.name = name;
        this.display = display;
        this.icon = icon;
        this.kitItems = kitItems;
        this.isActive = false;
    }


}
