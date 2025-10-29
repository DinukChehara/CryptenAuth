package me.tomqnto.cryptenAuth.config;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        save();
        loadConfig();
    }

    private void loadConfig() {
        plugin.reloadConfig();
    }

    public String getLoginMessage() {
        return plugin.getConfig().getString("login-message", "<red>Welcome back! Please log in using /login <password>.</red>");
    }

    public String getRegisterMessage() {
        return plugin.getConfig().getString("register-message", "<red>Welcome! Please register your account using /register <password> <confirm_password>.</red>");
    }

    public int getMinPasswordLength() {
        return plugin.getConfig().getInt("min-password-length", 6);
    }

    public int getSessionDurationMinutes() {
        return plugin.getConfig().getInt("session-duration-minutes", 30);
    }

    public int getLoginTimeoutSeconds() {
        return plugin.getConfig().getInt("login-timeout-seconds", 60);
    }

    public String getLoginTimeoutMessage() {
        return plugin.getConfig().getString("login-timeout-message", "<red>Time remaining: <seconds>s</red>");
    }

    public String getLoginTimeoutExpiredMessage() {
        return plugin.getConfig().getString("login-timeout-expired-message", "<red>Time limit expired! You have been kicked.</red>");
    }

    public boolean isLimitPlayersPerIpEnabled() {
        return plugin.getConfig().getBoolean("limit-players-per-ip.enabled", true);
    }

    public int getLimitPlayersPerIpMaxAccounts() {
        return plugin.getConfig().getInt("limit-players-per-ip.max-accounts", 1);
    }

    public String getLimitPlayersPerIpExceededMessage() {
        return plugin.getConfig().getString("limit-players-per-ip.exceeded-message", "<red>Maximum registered accounts from your location exceeded.</red>");
    }

    public String getStorageType() {
        return plugin.getConfig().getString("storage-type", "YAML");
    }

    public String getSqliteFilename() {
        return plugin.getConfig().getString("sqlite.filename", "playerdata.db");
    }

    public String getMysqlHost() {
        return plugin.getConfig().getString("mysql.host", "localhost");
    }

    public int getMysqlPort() {
        return plugin.getConfig().getInt("mysql.port", 3306);
    }

    public String getMysqlDatabase() {
        return plugin.getConfig().getString("mysql.database", "cryptenauth");
    }

    public String getMysqlUsername() {
        return plugin.getConfig().getString("mysql.username", "root");
    }

    public String getMysqlPassword() {
        return plugin.getConfig().getString("mysql.password", "password");
    }

    public void reload() {
        loadConfig();
    }

    public void save() {
        plugin.saveConfig();
    }

}
