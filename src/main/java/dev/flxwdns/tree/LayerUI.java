package dev.flxwdns.tree;

import dev.flxwdns.tree.annotation.Invoke;
import dev.flxwdns.tree.annotation.Manipulate;
import dev.flxwdns.tree.stack.StackUI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(fluent = true)
@SuppressWarnings("unused")
public final class LayerUI {
    private final Player player;
    private final Component title;
    private final ItemStack[] items = new ItemStack[54];
    private final Map<String, Object> manipulations;

    private Inventory inventory;
    private int rows;

    public LayerUI(Player player, Component title, Map<String, Object> manipulations) {
        this.rows = 6;
        this.player = player;
        this.title = title;
        this.manipulations = manipulations;

        var placeholder = ((String) this.manipulations.get("placeholder")).split("\\|")[0];
        if(placeholder.startsWith("empty")) {
            return;
        }
        var item = StackUI.of(Material.valueOf(((String) this.manipulations.get("placeholder")).split("\\|")[1].toUpperCase())).name(Component.empty());
        for (String s : placeholder.split(",")) {
            if(s.contains("-")) {
                var split = s.split("-");
                var start = Integer.parseInt(split[0]);
                var end = Integer.parseInt(split[1]);
                for (int i = start; i < end + 1; i++) {
                    item(i, item);
                }
            } else {
                item(Integer.parseInt(s), item);
            }
        }
    }

    public String grid() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 54; i++) {
            if (i % 9 == 0) {
                builder.append("\n");
            }

            if (this.items[i] == null) {
                builder.append("[AIR], ");
            } else {
                builder.append("[").append(this.items[i].getType()).append("], ");
            }
        }
        return builder.toString();
    }

    public void item(ItemStack stack) {
        for (int i = 0; i < 54; i++) {
            var item = this.items[i];
            if (item == null) {
                item(i, stack);
                return;
            }
        }
    }

    public void item(int row, int slot, ItemStack stack) {
        item((row - 1) * 9 + slot, stack);
    }

    public void item(int slot, ItemStack stack) {
        this.items[slot] = stack;

        if(inventory != null && !((Boolean) manipulations.get("refresh"))) {
            pushItems();
        }
    }

    public void pushItems() {
        if(inventory == null) {
            throw new RuntimeException("Inventory is not compiled yet!");
        }
        inventory.clear();
        inventory.setContents(items);
    }

    public Inventory compile() {
        var inventory = Bukkit.createInventory(player, rows * 9, title);
        inventory.setContents(items);
        this.inventory = inventory;
        return inventory;
    }

    public static Inventory compile(Player player, Object invObj) {
        return compile(player, invObj, 0);
    }

    public static Inventory compile(Player player, Object invObj, int id) {
        var inventory = invObj.getClass();
        for (Method method : inventory.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.isAnnotationPresent(Invoke.class)) {
                try {
                    var annotation = method.getAnnotation(Invoke.class);
                    if (annotation.id() == id) {
                        Map<String, Object> manipulations = new HashMap<>(Map.of(
                                "refresh", true,
                                "placeholder", "empty|black_stained_glass_pane",
                                "backward", "arrow|§7Zurück|false",
                                "forward", "arrow|§7Weiter|false"
                        ));

                        if(method.isAnnotationPresent(Manipulate.class)) {
                            var mani = method.getAnnotation(Manipulate.class).value();
                            for (String manipulation : mani.split(";")) {
                                var split = manipulation.split("\\(\\)");
                                var key = split[0];
                                var value = split[1];

                                if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                    manipulations.put(key, Boolean.valueOf(value));
                                } else {
                                    manipulations.put(key, value);
                                }
                            }
                        }

                        var compiler = new LayerUI(player, Component.text(annotation.name()), manipulations);
                        method.invoke(invObj, compiler);
                        return compiler.compile();
                    }
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
        throw new RuntimeException("UI class " + inventory.getSimpleName() + " has no methods that are annotated with @InvokeUI!");
    }

}
