package dev.twice.kits.managers;

import dev.twice.kits.KitsPlugin;
import dev.twice.kits.objects.KitData;
import dev.twice.kits.objects.Layout;
import dev.twice.kits.serialization.SerializationUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class DatabaseManager {

    private final KitsPlugin plugin;
    private String databasePath;
    private ExecutorService executor;

    public void initialize() {
        executor = Executors.newCachedThreadPool();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        databasePath = "jdbc:sqlite:" + new File(dataFolder, "database.db").getAbsolutePath();

        createTables();
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databasePath);
    }

    private void createTables() {
        executeAsync("CREATE TABLE IF NOT EXISTS kits (id TEXT PRIMARY KEY, contents TEXT NOT NULL)");
        executeAsync("CREATE TABLE IF NOT EXISTS layouts (id INTEGER PRIMARY KEY AUTOINCREMENT, kit TEXT NOT NULL, user TEXT NOT NULL, contents TEXT NOT NULL, UNIQUE(kit, user))");
    }

    public CompletableFuture<Void> insertKit(@NotNull KitData kitData) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO kits (id, contents) VALUES (?, ?)")) {
                ps.setString(1, kitData.getId());
                ps.setString(2, SerializationUtils.itemStackArrayToBase64(kitData.getContents()));
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to insert kit: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> insertLayout(@NotNull Layout layout) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO layouts (kit, user, contents) VALUES (?, ?, ?)")) {
                ps.setString(1, layout.getKit());
                ps.setString(2, layout.getOwner());
                ps.setString(3, SerializationUtils.itemStackArrayToBase64(layout.getContents()));
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to insert layout: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> updateLayout(@NotNull Layout layout) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE layouts SET contents=? WHERE kit=? AND user=?")) {
                ps.setString(1, SerializationUtils.itemStackArrayToBase64(layout.getContents()));
                ps.setString(2, layout.getKit());
                ps.setString(3, layout.getOwner());
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to update layout: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> deleteKit(@NotNull String id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM kits WHERE id=?")) {
                ps.setString(1, id);
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete kit: " + e.getMessage());
            }
        }, executor);
    }

    public CompletableFuture<Void> deleteLayouts(@NotNull String kit) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM layouts WHERE kit=?")) {
                ps.setString(1, kit);
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete layouts: " + e.getMessage());
            }
        }, executor);
    }

    private void executeAsync(@NotNull String query) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to execute query: " + e.getMessage());
            }
        }, executor);
    }

    public void loadKits() {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM kits");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                try {
                    KitManager.addKit(new KitData(id, SerializationUtils.itemStackArrayFromBase64(rs.getString("contents"))));
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to deserialize kit " + id + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load kits: " + e.getMessage());
        }
    }

    public void loadLayouts() {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM layouts");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String kit = rs.getString("kit");
                String user = rs.getString("user");
                String contents = rs.getString("contents");
                try {
                    LayoutManager.putLayout(new Layout(SerializationUtils.itemStackArrayFromBase64(contents), kit, user));
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to deserialize layout for " + user + " kit " + kit + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load layouts: " + e.getMessage());
        }
    }
}