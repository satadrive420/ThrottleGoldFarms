package gg.corn.throttlegoldfarms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    private final ThrottleGoldFarms plugin;

    public CommandHandler(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Send usage message if no arguments are provided
            sender.sendMessage(Component.text("Usage: /" + label + " [reload|getrate]").color(NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(Component.text("ThrottleGoldFarms configuration reloaded.").color(NamedTextColor.GREEN));
            return true;
        } else if (args[0].equalsIgnoreCase("getrate")) {
            double currentSpawnRate = plugin.getCurrentSpawnRate();
            sender.sendMessage(Component.text("Current spawn rate: " + currentSpawnRate + "%").color(NamedTextColor.GREEN));
            return true;
        }

        // If the argument doesn't match known subcommands
        sender.sendMessage(Component.text("Invalid command. Usage: /" + label + " [reload|getrate]").color(NamedTextColor.RED));
        return true;
    }

}
