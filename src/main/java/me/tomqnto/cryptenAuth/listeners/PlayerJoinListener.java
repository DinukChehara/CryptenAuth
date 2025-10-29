package me.tomqnto.cryptenAuth.listeners;

import me.tomqnto.cryptenAuth.CryptenAuth;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerJoinListener implements Listener {

    private final CryptenAuth plugin;

    public PlayerJoinListener(CryptenAuth plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDataManager().isRegistered(player.getUniqueId())) {
            // Player is registered
            if (plugin.getSessionManager().isValidSession(player.getUniqueId())) {
                String currentIp = player.getAddress().getAddress().getHostAddress();
                String lastLoginIp = plugin.getDataManager().getLastLoginIp(player.getUniqueId());

                if (lastLoginIp != null && !currentIp.equals(lastLoginIp)) {
                    // IP address changed, force re-login
                    plugin.getSessionManager().removeSession(player.getUniqueId());
                    player.sendMessage(plugin.getMiniMessage().deserialize("<red>Your IP address has changed. Please log in again.</red>"));
                    player.sendMessage(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getLoginMessage()));
                } else {
                    // Player has a valid session, allow them to play
                    player.sendMessage(plugin.getMiniMessage().deserialize("<green>Welcome back! You are automatically logged in.</green>"));
                }
            } else {
                // Player is registered but session expired, needs to log in
                player.sendMessage(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getLoginMessage()));
                plugin.startLoginTimeoutTask(player);
            }
        } else {
            // Player is not registered, needs to register
            player.sendMessage(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getRegisterMessage()));
            plugin.startLoginTimeoutTask(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            // Prevent movement if not logged in/registered
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to chat.</red>"));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to break blocks.</red>"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to place blocks.</red>"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to interact.</red>"));
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/register") && !command.startsWith("/login") && !command.startsWith("/unregister")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to use commands.</red>"));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to hit entities.</red>"));
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to pick up items.</red>"));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Please register or log in to drop items.</red>"));
        }
    }
}
