package gg.corn.throttlegoldfarms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PigZombie;

public class ReinforcementSpawnListener implements Listener {

    private final ThrottleGoldFarms plugin;

    public ReinforcementSpawnListener(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPigZombieReinforcementSpawn(CreatureSpawnEvent event) {
        // Check if the config option is enabled
        if (!plugin.getConfig().getBoolean("prevent-overworld-reinforcements", true)) {
            return;
        }

        // Only handle PigZombie reinforcements
        if (!(event.getEntity() instanceof PigZombie)) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS) {
            return;
        }

        // Check if the spawn is occurring in the Overworld
        if (event.getLocation().getWorld().getEnvironment() == World.Environment.NORMAL) {
            event.setCancelled(true);
            Bukkit.getLogger().info("[TGF] Cancelled overworld reinforcement PigZombie spawn.");
        }
    }
}