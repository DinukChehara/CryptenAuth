package me.tomqnto.cryptenAuth;

import me.tomqnto.cryptenAuth.commands.LoginCommand;
import me.tomqnto.cryptenAuth.commands.RegisterCommand;
import me.tomqnto.cryptenAuth.commands.ReloadCommand;
import me.tomqnto.cryptenAuth.commands.UnregisterCommand;
import me.tomqnto.cryptenAuth.data.*;
import me.tomqnto.cryptenAuth.listeners.PlayerJoinListener;
import me.tomqnto.cryptenAuth.config.PluginConfig;
import me.tomqnto.cryptenAuth.util.SessionManager;
import me.tomqnto.cryptenAuth.util.LoginTimeoutTask;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CryptenAuth extends JavaPlugin {

    private PluginConfig pluginConfig;
    private DataManager dataManager;
    private SessionManager sessionManager;
    private MiniMessage miniMessage;
    private Map<UUID, LoginTimeoutTask> loginTimeoutTasks;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("CryptenAuth has been enabled!");

        // Initialize MiniMessage
        this.miniMessage = MiniMessage.miniMessage();

        // Initialize configuration
        this.pluginConfig = new PluginConfig(this);

        // Initialize Data Manager
        String storageType = pluginConfig.getStorageType().toUpperCase();
        switch (storageType) {
            case "YAML":
                this.dataManager = new YAMLDataManager(this);
                break;
            case "SQLITE":
                this.dataManager = new SQLiteDataManager(this);
                break;
            case "MYSQL":
                this.dataManager = new MySQLDataManager(this);
                break;
            default:
                getLogger().severe("Invalid storage type specified in config.yml: " + storageType + ". Defaulting to YAML.");
                this.dataManager = new YAMLDataManager(this);
                break;
        }

        // Initialize Session Manager
        this.sessionManager = new SessionManager(this);

        // Initialize Login Timeout Tasks map
        this.loginTimeoutTasks = new HashMap<>();

        // Register Commands
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("cryptenauthreload").setExecutor(new ReloadCommand(this));
        getCommand("unregister").setExecutor(new UnregisterCommand(this));

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("CryptenAuth has been disabled!");
        if (pluginConfig != null) {
            pluginConfig.save(); // Save config changes
        }
        if (dataManager != null) {
            dataManager.saveData();
            if (dataManager instanceof SQLDataManager) {
                ((SQLDataManager) dataManager).closeConnection();
            }
        }
        if (sessionManager != null) {
            sessionManager.clearSessions();
        }
        // Cancel all running login timeout tasks
        for (LoginTimeoutTask task : loginTimeoutTasks.values()) {
            task.cancel();
        }
        loginTimeoutTasks.clear();
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    /**
     * Starts a login timeout task for a player.
     * If a task already exists for the player, it will be reset.
     * @param player The player to start the task for.
     */
    public void startLoginTimeoutTask(Player player) {
        UUID playerUUID = player.getUniqueId();
        LoginTimeoutTask existingTask = loginTimeoutTasks.get(playerUUID);
        if (existingTask != null) {
            existingTask.resetTime();
        } else {
            LoginTimeoutTask newTask = new LoginTimeoutTask(this, playerUUID);
            newTask.runTaskTimer(this, 0L, 20L); // Run every second (20 ticks)
            loginTimeoutTasks.put(playerUUID, newTask);
        }
    }

    /**
     * Removes and cancels the login timeout task for a player.
     * @param playerUUID The UUID of the player.
     */
    public void removeLoginTimeoutTask(UUID playerUUID) {
        LoginTimeoutTask task = loginTimeoutTasks.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
    }
}
