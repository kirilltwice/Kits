package dev.twice.kits.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class Layout {
    private ItemStack[] contents;
    private final String kit;
    private final String owner;

    public void set(int slot, ItemStack item) {
        if (slot >= 0 && slot < contents.length) {
            this.contents[slot] = item;
        }
    }
}
