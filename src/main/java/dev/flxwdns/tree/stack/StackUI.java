package dev.flxwdns.tree.stack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

@SuppressWarnings("unused")
public class StackUI extends ItemStack {
    public StackUI(Material material) {
        super(material);
    }

    public StackUI(ItemStack itemStack) {
        super(itemStack);
    }

    public StackUI plainName(String name) {
        var meta = getItemMeta();
        meta.displayName(Component.text("§r§7" + name).decoration(TextDecoration.ITALIC, false));
        setItemMeta(meta);
        return this;
    }

    public StackUI name(Component component) {
        var meta = getItemMeta();
        meta.displayName(component.decoration(TextDecoration.ITALIC, false));
        setItemMeta(meta);
        return this;
    }

    public StackUI plainList(List<String> lines) {
        var meta = getItemMeta();
        meta.lore(lines.stream().map(it -> Component.text("§r§7" + it).decoration(TextDecoration.ITALIC, false)).toList());
        setItemMeta(meta);
        return this;
    }

    public StackUI list(List<Component> lines) {
        var meta = getItemMeta();
        meta.lore(lines.stream().map(it -> it.decoration(TextDecoration.ITALIC, false)).toList());
        setItemMeta(meta);
        return this;
    }

    public StackUI amount(int amount) {
        setAmount(amount);
        return this;
    }

    public StackUI glow() {
        addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        var meta = getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setItemMeta(meta);
        return this;
    }

    public StackUI enchant(Enchantment enchantment, int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    @SuppressWarnings("all")
    public StackUI flag(ItemFlag itemFlag) {
        var meta = getItemMeta();
        meta.addItemFlags(itemFlag);
        setItemMeta(meta);
        return this;
    }

    public StackUI flags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            flag(itemFlag);
        }
        return this;
    }

    public StackUI leatherColor(Color color) {
        var meta = (LeatherArmorMeta) getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }


    public static StackUI of(Material material) {
        return new StackUI(material);
    }

    public static StackUI of(ItemStack itemStack) {
        return new StackUI(itemStack);
    }
}
