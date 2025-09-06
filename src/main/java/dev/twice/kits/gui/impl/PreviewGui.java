package dev.twice.kits.gui.impl;

import dev.twice.kits.KitsPlugin;
import dev.twice.kits.gui.InventoryGui;
import dev.twice.kits.objects.KitData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PreviewGui extends InventoryGui {
    private static ItemStack empty = null;
    private final KitData kitData;

    public PreviewGui(@NotNull Player player, @NotNull KitData kitData) {
        super(player, 45, Component.text(KitsPlugin.getMessage("lang.preview-title")));
        this.kitData = kitData;
        create();
    }

    @Override
    public void onCreate() {
        ItemStack[] contents = kitData.getContents();

        for (int i = 0; i < 36; i++) {
            setItem(i, contents[i]);
        }

        for (int i = 36; i < 45; i++) {
            if (i > 37 && i < 43) {
                setItem(i, contents[i - 2]);
            } else {
                setItem(i, getEmptyItem());
            }
        }
    }

    @NotNull
    private ItemStack getEmptyItem() {
        if (empty != null) {
            return empty;
        }

        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.YELLOW_STAINED_GLASS_PANE);
        meta.displayName(Component.text(" ").color(TextColor.color(0, 0, 0)));
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        empty = item;
        return item;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }
}