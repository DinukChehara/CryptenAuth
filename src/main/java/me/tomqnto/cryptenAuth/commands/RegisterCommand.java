package me.tomqnto.cryptenAuth.commands;

import me.tomqnto.cryptenAuth.CryptenAuth;
import me.tomqnto.cryptenAuth.util.PasswordUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    private final CryptenAuth plugin;

    public RegisterCommand(CryptenAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Only players can use this command.</red>"));
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getDataManager().isRegistered(player.getUniqueId())) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>You are already registered. Please log in with /login <password>.</red>"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /register <password> <confirm_password></red>"));
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Passwords do not match.</red>"));
            return true;
        }

        if (password.length() < plugin.getPluginConfig().getMinPasswordLength()) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Password must be at least " + plugin.getPluginConfig().getMinPasswordLength() + " characters long.</red>"));
            return true;
        }

        String ipAddress = player.getAddress().getAddress().getHostAddress();
        if (plugin.getPluginConfig().isLimitPlayersPerIpEnabled()) {
            int registeredCount = plugin.getDataManager().getRegisteredAccountsCountByIp(ipAddress);
            if (registeredCount >= plugin.getPluginConfig().getLimitPlayersPerIpMaxAccounts()) {
                player.sendMessage(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getLimitPlayersPerIpExceededMessage()));
                return true;
            }
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        if (plugin.getDataManager().registerPlayer(player.getUniqueId(), player.getName(), hashedPassword, ipAddress)) {
            plugin.getSessionManager().createSession(player.getUniqueId());
            plugin.removeLoginTimeoutTask(player.getUniqueId()); // Cancel timeout task
            player.sendMessage(plugin.getMiniMessage().deserialize("<green>You have successfully registered and are now logged in!</green>"));
        } else {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>An error occurred during registration. Please try again.</red>"));
        }

        return true;
    }
}
