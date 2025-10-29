package me.tomqnto.cryptenAuth.commands;

import me.tomqnto.cryptenAuth.CryptenAuth;
import me.tomqnto.cryptenAuth.util.PasswordUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnregisterCommand implements CommandExecutor {

    private final CryptenAuth plugin;

    public UnregisterCommand(CryptenAuth plugin) {
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
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>You are not registered.</red>"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /unregister <password></red>"));
            return true;
        }

        String password = args[0];
        String hashedPassword = plugin.getDataManager().getHashedPassword(player.getUniqueId());

        if (hashedPassword == null || !PasswordUtil.checkPassword(password, hashedPassword)) {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>Incorrect password.</red>"));
            return true;
        }

        // Password is correct, proceed with unregistration
        if (plugin.getDataManager().removePlayer(player.getUniqueId())) {
            plugin.getSessionManager().removeSession(player.getUniqueId());
            plugin.removeLoginTimeoutTask(player.getUniqueId()); // Cancel timeout task
            player.sendMessage(plugin.getMiniMessage().deserialize("<green>You have successfully unregistered your account.</green>"));
            player.sendMessage(plugin.getMiniMessage().deserialize(plugin.getPluginConfig().getRegisterMessage())); // Prompt to register again
        } else {
            player.sendMessage(plugin.getMiniMessage().deserialize("<red>An error occurred during unregistration. Please try again.</red>"));
        }

        return true;
    }
}
