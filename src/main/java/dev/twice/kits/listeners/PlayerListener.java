package dev.twice.kits.listeners;

import dev.twice.kits.managers.EditorManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        EditorManager.removeEditor(event.getPlayer());
        InventoryListener.reset(event.getPlayer());
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (EditorManager.isEditor(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
