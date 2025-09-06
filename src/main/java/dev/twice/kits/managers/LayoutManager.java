package dev.twice.kits.managers;

import dev.twice.kits.objects.KitData;
import dev.twice.kits.objects.Layout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayoutManager {

    public static void putLayout(@NotNull Layout layout) {
        KitData kitData = KitManager.getKit(layout.getKit());
        if (kitData != null) {
            kitData.putLayout(layout);
        }
    }

    public static boolean hasLayout(@NotNull String user, @NotNull String kit) {
        KitData kitData = KitManager.getKit(kit);
        return kitData != null && kitData.getLayout(user) != null;
    }

    @Nullable
    public static Layout getLayout(@NotNull String user, @NotNull String kit) {
        KitData kitData = KitManager.getKit(kit);
        return kitData != null ? kitData.getLayout(user) : null;
    }
}