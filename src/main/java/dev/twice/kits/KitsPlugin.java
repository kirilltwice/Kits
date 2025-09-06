package dev.twice.kits;

import dev.twice.kits.commands.KitCommand;
import dev.twice.kits.commands.KitsCommand;
import dev.twice.kits.listeners.InventoryListener;
import dev.twice.kits.listeners.PlayerListener;
import dev.twice.kits.managers.DatabaseManager;
import dev.twice.kits.util.ChatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class KitsPlugin extends JavaPlugin {

    private static final Map<String, Long> cooldowns = new HashMap<>();

    @Getter
    private static KitsPlugin instance;

    @Getter
    private static DatabaseManager database;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        database = new DatabaseManager(this);
        database.initialize();
        database.loadKits();
        database.loadLayouts();

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("kit").setExecutor(new KitCommand());
        getCommand("kits").setExecutor(new KitsCommand());
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.shutdown();
        }
    }

    public static boolean checkCooldown(String playerName) {
        int cooldownTime = getConf().getInt("settings.kits-delay");
        if (cooldowns.containsKey(playerName)) {
            long left = cooldowns.get(playerName) / 1000L + cooldownTime - System.currentTimeMillis() / 1000L;
            if (left > 0L) {
                return false;
            }
        }
        cooldowns.put(playerName, System.currentTimeMillis());
        return true;
    }

    public static FileConfiguration getConf() {
        return instance.getConfig();
    }

    public static String getPrefix() {
        return ChatUtil.color(instance.getConfig().getString("lang.prefix"));
    }

    public static String getMessage(String path) {
        return ChatUtil.color(instance.getConfig().getString(path)).replace("{prefix}", getPrefix());
    }
}