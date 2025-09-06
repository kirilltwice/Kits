package dev.twice.kits.managers;

import dev.twice.kits.gui.impl.EditorGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EditorManager {
    private static final Map<Player, EditorGui> editors = new HashMap<>();

    public static boolean isEditor(@NotNull Player player) {
        return editors.containsKey(player);
    }

    public static void removeEditor(@NotNull Player player) {
        editors.remove(player);
    }

    public static void setEditor(@NotNull Player player, @NotNull EditorGui gui) {
        editors.put(player, gui);
    }

    @Nullable
    public static EditorGui getEditor(@NotNull Player player) {
        return editors.get(player);
    }
}