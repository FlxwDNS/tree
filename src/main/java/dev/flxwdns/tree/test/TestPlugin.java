package dev.flxwdns.tree.test;

import dev.flxwdns.tree.LayerUI;
import dev.flxwdns.tree.annotation.Invoke;
import dev.flxwdns.tree.annotation.Manipulate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        var inventory = LayerUI.compile(null, this, 2);
        System.out.println(inventory.getSize() / 9);
    }

    @Invoke(name = "Single")
    public void single(LayerUI ui) {
        ui.item(new ItemStack(Material.DIRT));

        Bukkit.getScheduler().runTaskLater(this, () -> {
            ui.item(new ItemStack(Material.APPLE));

            System.out.println(ui.grid());
        }, 60);

        ui.item(1, new ItemStack(Material.DIAMOND));

        System.out.println(ui.grid());
    }

    /**
     * manipulations:
     * refresh=[true,false]
     */
    @Invoke(name = "Manipulate", id = 1)
    @Manipulate("refresh()false;")
    public void manipulate(LayerUI ui) {
        ui.item(new ItemStack(Material.GLASS));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            ui.item(new ItemStack(Material.GRASS_BLOCK));
            System.out.println(ui.grid());
        }, 60);

        System.out.println(ui.grid());
    }

    /**
     * manipulations:
     * backward=[stack|name|alwaysThere]
     * forward=[stack|name|alwaysThere]
     */
    @Invoke(name = "List", id = 2)
    @Manipulate("backward()arrow|§cZurück|true;forward()arrow|§cWeiter|true;placeholder()9-17,19,27-35|paper")
    public void list(LayerUI ui) {
        ui.item(new ItemStack(Material.GLASS));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            ui.item(new ItemStack(Material.GRASS_BLOCK));
            System.out.println(ui.grid());
        }, 60);

        System.out.println(ui.grid());
    }
}
