package me.tomqnto.cryptenAuth.commands;

import me.tomqnto.cryptenAuth.CryptenAuth;
import me.tomqnto.cryptenAuth.util.PasswordUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final CryptenAuth plugin;

    public LoginCommand(CryptenAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Only players can use this command.</red>"));
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.getDataManager().isRegistered(player.getUniqueId())) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>You are not registered. Please register with /register <password> <confirm_password>.</red>"));
            return true;
        }

        if (plugin.getSessionManager().isValidSession(player.getUniqueId())) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<green>You are already logged in.</green>"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /login <password></red>"));
            return true;
        }

        String password = args[0];
        String hashedPassword = plugin.getDataManager().getHashedPassword(player.getUniqueId());

        if (hashedPassword == null || !PasswordUtil.checkPassword(password, hashedPassword)) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Incorrect password.</red>"));
            return true;
        }

        plugin.getSessionManager().createSession(player.getUniqueId());
        plugin.getDataManager().updateLastLoginIp(player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
        plugin.removeLoginTimeoutTask(player.getUniqueId()); // Cancel timeout task
        player.sendMessage(plugin.getMiniMessage().deserialize("<green>You have successfully logged in!</green>"));

        return true;
    }
}
