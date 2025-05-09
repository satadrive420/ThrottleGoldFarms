package gg.corn.throttlegoldfarms;

import org.bukkit.plugin.java.JavaPlugin;

public final class ThrottleGoldFarms extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PortalSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new SwordDropRemover(this), this);
        getServer().getPluginManager().registerEvents(new ReinforcementSpawnListener(this), this);
        this.getCommand("throttlegoldfarms").setExecutor(new CommandHandler(this));
        this.getCommand("resetspawnrate").setExecutor(new ResetSpawnRateCommand(this));
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public double getCurrentSpawnRate() {
        // Implement logic to return the current spawn rate
        // This could be the last adjusted spawn rate or the base spawn rate from config
        return PortalSpawnListener.lastAdjustedSpawnRate; // or some other relevant value
    }
}
