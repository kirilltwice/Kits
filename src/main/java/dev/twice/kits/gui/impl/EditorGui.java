package dev.twice.kits.gui.impl;

import dev.twice.kits.KitsPlugin;
import dev.twice.kits.gui.InventoryGui;
import dev.twice.kits.managers.EditorManager;
import dev.twice.kits.managers.LayoutManager;
import dev.twice.kits.objects.Layout;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class EditorGui extends InventoryGui {
    private static ItemStack empty = null;

    @Getter
    private final String id;
    private final Layout layout;

    public EditorGui(@NotNull Player player, @NotNull String id, ItemStack @NotNull [] contents) {
        super(player, 45, Component.text(KitsPlugin.getMessage("lang.edit-title")));
        this.id = id;
        this.layout = new Layout(contents.clone(), id, player.getName());
        create();
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        EditorManager.removeEditor(player);

        if (!player.getItemOnCursor().getType().isAir()) {
            inventory.addItem(player.getItemOnCursor());
            player.setItemOnCursor(null);
        }

        for (int i = 0; i < 36; i++) {
            layout.set(i, getItem(i));
        }
        layout.set(40, getItem(40));

        if (LayoutManager.hasLayout(player.getName(), id)) {
            KitsPlugin.getDatabase().updateLayout(layout);
        } else {
            KitsPlugin.getDatabase().insertLayout(layout);
        }

        LayoutManager.putLayout(layout);
        player.sendMessage(Component.text(KitsPlugin.getMessage("lang.edit-success")));
    }

    @Override
    public void onCreate() {
        ItemStack[] contents = layout.getContents();

        for (int i = 0; i < 36; i++) {
            setItem(i, contents[i]);
        }

        for (int i = 36; i < 45; i++) {
            setItem(i, getEmptyItem());
        }

        setOffHand(contents[40]);
    }

    public void setOffHand(ItemStack item) {
        setItem(40, item);
    }

    @NotNull
    private ItemStack getEmptyItem() {
        if (empty != null) {
            return empty;
        }

        ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.WHITE_STAINED_GLASS_PANE);
        meta.displayName(Component.text(" ").color(TextColor.color(0, 0, 0)));
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        empty = item;
        return item;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !inventory.equals(event.getClickedInventory())) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getSlot();
        if (slot >= 36 && slot != 40) {
            event.setCancelled(true);
            return;
        }

        switch (event.getAction()) {
            case SWAP_WITH_CURSOR:
            case PLACE_ALL:
            case PICKUP_ALL:
                break;
            default:
                event.setCancelled(true);
        }
    }
}
