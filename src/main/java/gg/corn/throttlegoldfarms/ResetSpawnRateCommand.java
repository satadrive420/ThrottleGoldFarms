package gg.corn.throttlegoldfarms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSpawnRateCommand implements CommandExecutor {

    private ThrottleGoldFarms plugin;

    public ResetSpawnRateCommand(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("throttlegoldfarms.resetspawnrate")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }


        PortalSpawnListener.resetSpawnRate();
        player.sendMessage("The spawn rate has been reset to 100%.");

        return true;
    }
}