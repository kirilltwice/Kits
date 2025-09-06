package dev.twice.kits.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public abstract class InventoryGui {
    protected final Player player;
    protected final int size;
    protected final Component title;
    protected Inventory inventory;

    public void create() {
        this.inventory = Bukkit.createInventory(null, size, title);
        onCreate();
    }

    public void onCreate() {}

    public void open() {
        if (!player.getItemOnCursor().getType().isAir()) {
            ItemStack cursor = player.getItemOnCursor();
            player.getInventory().addItem(cursor);
            player.setItemOnCursor(null);
        }

        player.openInventory(inventory);
        Holders.add(this);
        onOpen();
    }

    public void onOpen() {}

    public void close() {
        Holders.remove(this);
    }

    public void onClose(@NotNull InventoryCloseEvent event) {}

    public void setItem(int pos, ItemStack itemStack) {
        inventory.setItem(pos, itemStack);
    }

    public ItemStack getItem(int pos) {
        return inventory.getItem(pos);
    }

    public void update() {
        player.updateInventory();
    }

    public abstract void onClick(@NotNull InventoryClickEvent event);

    public static void closeInventories() {
        Holders.getGuis().forEach(inv -> inv.getPlayer().closeInventory());
    }
}