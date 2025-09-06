package dev.twice.kits.managers;

import dev.twice.kits.objects.KitData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KitManager {
    private static final Map<String, KitData> kits = new HashMap<>();

    @NotNull
    public static Set<String> getAllowedKits(@NotNull CommandSender sender) {
        Set<String> allowedKits = new HashSet<>();
        for (String kitName : getKits().keySet()) {
            if (sender.hasPermission("kits.kit." + kitName)) {
                allowedKits.add(kitName);
            }
        }
        return allowedKits;
    }

    @Nullable
    public static KitData getKit(@NotNull String kitName) {
        return kits.get(kitName);
    }

    public static void addKit(@NotNull KitData data) {
        kits.put(data.getId(), data);
    }

    public static void delKit(@NotNull String name) {
        kits.remove(name);
    }

    @NotNull
    public static Map<String, KitData> getKits() {
        return kits;
    }
}