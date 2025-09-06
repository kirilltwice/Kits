package dev.twice.kits.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownUtil<T> {
    private final Map<T, Long> cooldowns = new ConcurrentHashMap<>();

    public boolean isCooldown(@NotNull T player, long time) {
        if (cooldowns.containsKey(player)) {
            long left = cooldowns.get(player) + time - System.currentTimeMillis();
            if (left >= 0L) {
                return true;
            }
        }

        cooldowns.put(player, System.currentTimeMillis());
        return false;
    }

    public void reset() {
        cooldowns.clear();
    }

    public void reset(@NotNull T player) {
        cooldowns.remove(player);
    }
}