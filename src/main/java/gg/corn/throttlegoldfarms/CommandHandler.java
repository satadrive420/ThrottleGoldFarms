package gg.corn.throttlegoldfarms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class CommandHandler implements CommandExecutor {

    private final ThrottleGoldFarms plugin;

    public CommandHandler(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Send usage message if no arguments are provided
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [reload|getrate]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "ThrottleGoldFarms configuration reloaded.");
            return true;
        } else if (args[0].equalsIgnoreCase("getrate")) {
            double currentSpawnRate = plugin.getCurrentSpawnRate(); // Implement this method in MyPlugin
            sender.sendMessage(ChatColor.GREEN + "Current spawn rate: " + currentSpawnRate + "%");
            return true;
        }

        // If the argument doesn't match known subcommands
        sender.sendMessage(ChatColor.RED + "Invalid command. Usage: /" + label + " [reload|getrate]");
        return true;
    }
}