package dev.twice.kits.gui;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Holders {
    private static final Map<UUID, InventoryGui> guis = new HashMap<>();

    public static void add(@NotNull InventoryGui inventoryGui) {
        guis.put(inventoryGui.getPlayer().getUniqueId(), inventoryGui);
    }

    public static void remove(@NotNull InventoryGui inventoryGui) {
        guis.remove(inventoryGui.getPlayer().getUniqueId());
    }

    @Nullable
    public static InventoryGui getGuiByPlayer(@NotNull UUID uuid) {
        return guis.get(uuid);
    }

    @NotNull
    public static Collection<InventoryGui> getGuis() {
        return guis.values();
    }
}