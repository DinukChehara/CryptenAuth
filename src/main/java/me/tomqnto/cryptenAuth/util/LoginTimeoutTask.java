package me.tomqnto.cryptenAuth.util;

import me.tomqnto.cryptenAuth.CryptenAuth;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LoginTimeoutTask extends BukkitRunnable {

    private final CryptenAuth plugin;
    private final UUID playerUUID;
    private int timeLeft;

    public LoginTimeoutTask(CryptenAuth plugin, UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.timeLeft = plugin.getPluginConfig().getLoginTimeoutSeconds();
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            cancel();
            plugin.removeLoginTimeoutTask(playerUUID);
            return;
        }

        if (plugin.getSessionManager().isValidSession(playerUUID)) {
            // Player has logged in/registered, cancel task
            cancel();
            plugin.removeLoginTimeoutTask(playerUUID);
            return;
        }

        if (timeLeft <= 0) {
            // Time's up, kick the player
            player.kick(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getLoginTimeoutExpiredMessage()));
            cancel();
            plugin.removeLoginTimeoutTask(playerUUID);
            return;
        }

        // Send action bar message
        String message = plugin.getPluginConfig().getLoginTimeoutMessage().replace("<seconds>", String.valueOf(timeLeft));
        player.sendActionBar(plugin.getMiniMessage().deserialize(message));

        timeLeft--;
    }

    public void resetTime() {
        this.timeLeft = plugin.getPluginConfig().getLoginTimeoutSeconds();
    }
}
