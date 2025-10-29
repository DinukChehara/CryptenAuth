package me.tomqnto.cryptenAuth.data;

import me.tomqnto.cryptenAuth.CryptenAuth;
import me.tomqnto.cryptenAuth.config.PluginConfig;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SQLiteDataManager implements DataManager, SQLDataManager {

    private final CryptenAuth plugin;
    private final PluginConfig config;
    private Connection connection;

    public SQLiteDataManager(CryptenAuth plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        initializeDatabase();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                File dataFolder = new File(plugin.getDataFolder(), config.getSqliteFilename());
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath());
            } catch (ClassNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, "SQLite JDBC driver not found!", e);
            }
        }
        return connection;
    }

    @Override
    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS players (" +
                         "uuid VARCHAR(36) PRIMARY KEY," +
                         "player_name VARCHAR(16) NOT NULL," +
                         "hashed_password VARCHAR(60) NOT NULL," +
                         "last_ip VARCHAR(45) NOT NULL" +
                         ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error initializing SQLite database: " + e.getMessage(), e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing SQLite connection: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean registerPlayer(UUID uuid, String playerName, String hashedPassword, String ipAddress) {
        if (isRegistered(uuid)) {
            return false;
        }
        String sql = "INSERT INTO players(uuid, player_name, hashed_password, last_ip) VALUES(?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, playerName);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, ipAddress);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error registering player in SQLite: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        String sql = "SELECT uuid FROM players WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error checking if player is registered in SQLite: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getHashedPassword(UUID uuid) {
        String sql = "SELECT hashed_password FROM players WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("hashed_password");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error getting hashed password from SQLite: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean updatePassword(UUID uuid, String newHashedPassword) {
        String sql = "UPDATE players SET hashed_password = ? WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, uuid.toString());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating password in SQLite: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateLastLoginIp(UUID uuid, String ipAddress) {
        String sql = "UPDATE players SET last_ip = ? WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            pstmt.setString(2, uuid.toString());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating last login IP in SQLite: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getLastLoginIp(UUID uuid) {
        String sql = "SELECT last_ip FROM players WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("last_ip");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error getting last login IP from SQLite: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int getRegisteredAccountsCountByIp(String ipAddress) {
        String sql = "SELECT COUNT(uuid) FROM players WHERE last_ip = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error getting registered accounts count by IP from SQLite: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        String sql = "DELETE FROM players WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error removing player from SQLite: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void saveData() {
        // Data is saved immediately with each operation, so this can be empty or used for a final flush if needed.
    }

    @Override
    public void loadData() {
        // Data is loaded on demand, or the database is always accessible. No need to load all into memory.
    }
}
