package me.tomqnto.cryptenAuth.commands;

import me.tomqnto.cryptenAuth.CryptenAuth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final CryptenAuth plugin;

    public ReloadCommand(CryptenAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("cryptenauth.command.reload")) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>You do not have permission to use this command.</red>"));
            return true;
        }

        plugin.getPluginConfig().save(); // Save current config before reloading
        plugin.getPluginConfig().reload();
        sender.sendMessage(plugin.getMiniMessage().deserialize("<green>CryptenAuth configuration reloaded successfully!</green>"));
        return true;
    }
}
