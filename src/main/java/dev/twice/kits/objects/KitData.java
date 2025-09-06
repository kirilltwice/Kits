package dev.twice.kits.objects;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Data
public class KitData {
    private final Map<String, Layout> layouts = new HashMap<>();
    private ItemStack[] contents;
    private final String id;

    public KitData(@NotNull String id, ItemStack[] contents) {
        this.id = id;
        this.contents = contents;
    }

    public KitData(@NotNull PlayerInventory playerInv, @NotNull String id) {
        this.id = id;
        this.load(playerInv);
    }

    public void load(@NotNull PlayerInventory inv) {
        this.contents = inv.getContents();
    }

    public void save(@NotNull PlayerInventory inv, @Nullable Layout layout) {
        if (layout == null) {
            inv.setContents(this.contents);
        } else {
            inv.setContents(layout.getContents());
        }
    }

    public void putLayout(@NotNull Layout layout) {
        this.layouts.put(layout.getOwner(), layout);
    }

    @Nullable
    public Layout getLayout(@NotNull String user) {
        return this.layouts.get(user);
    }
}