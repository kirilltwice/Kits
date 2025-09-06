package dev.twice.kits.listeners;

import dev.twice.kits.gui.Holders;
import dev.twice.kits.gui.InventoryGui;
import dev.twice.kits.util.CooldownUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InventoryListener implements Listener {
    private static final CooldownUtil<Player> util = new CooldownUtil<>();

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        InventoryGui gui = Holders.getGuiByPlayer(uuid);
        if (gui != null) {
            gui.onClose(event);
            gui.close();
        }
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        InventoryGui gui = Holders.getGuiByPlayer(uuid);
        if (gui != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        InventoryGui gui = Holders.getGuiByPlayer(uuid);
        if (gui != null) {
            if (util.isCooldown(gui.getPlayer(), 50L)) {
                event.setCancelled(true);
                return;
            }
            gui.onClick(event);
        }
    }

    public static void reset() {
        util.reset();
    }

    public static void reset(@NotNull Player player) {
        util.reset(player);
    }
}