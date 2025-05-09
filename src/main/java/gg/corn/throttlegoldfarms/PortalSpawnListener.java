package gg.corn.throttlegoldfarms;



import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PigZombie;
import org.bukkit.metadata.FixedMetadataValue;

import static org.bukkit.Bukkit.getServer;

public class PortalSpawnListener implements Listener {

    private static ThrottleGoldFarms plugin;

    public static double lastAdjustedSpawnRate;

    public PortalSpawnListener(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
        lastAdjustedSpawnRate = 95;
    }

    private long lastDecreaseTime = 0; // Timestamp of the last decrease
    private long lastIncreaseTime = 0; // Timestamp of the last increase


    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        boolean verbose = plugin.getConfig().getBoolean("verbose-logging");

        // Check if the entity spawned is a PigZombie
        if (event.getEntity() instanceof PigZombie) {
            PigZombie pigZombie = (PigZombie) event.getEntity();

            // Check if the spawn reason is through a Nether Portal
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL) {
                double currentMSPT = getCurrentMSPT();
                double adjustedSpawnRate = calculateSpawnRate(currentMSPT);

                // Cancel the event based on your adjusted spawn rate condition
                if (Math.random() * 100 > adjustedSpawnRate) {
                    if (verbose) {
                        Bukkit.getLogger().info("[TGF] Successfully cancelled a portal spawn!");
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    private double getCurrentMSPT() {
        return getServer().getAverageTickTime();
    }

    private double calculateSpawnRate(double currentMSPT) {
        long INCREASE_DELAY_TICKS = plugin.getConfig().getLong("increase-delay");
        long DECREASE_DELAY_TICKS = plugin.getConfig().getLong("decrease-delay");
        double minMSPT = plugin.getConfig().getDouble("minMSPT");
        double maxMSPT = plugin.getConfig().getDouble("maxMSPT");
        double baseSpawnRate = plugin.getConfig().getDouble("baseSpawnRate");
        double newSpawnRate = 100;

        if (currentMSPT <= minMSPT) {
            newSpawnRate = baseSpawnRate;
        } else if (currentMSPT >= maxMSPT) {
            newSpawnRate = 0.01;
        } else {
            newSpawnRate = baseSpawnRate * (1 - (currentMSPT - minMSPT) / (maxMSPT - minMSPT));
        }

        long currentTime = System.currentTimeMillis();

        // Check if the spawn rate is being decreased
        if (newSpawnRate < lastAdjustedSpawnRate) {
            if ((currentTime - lastDecreaseTime) < DECREASE_DELAY_TICKS * 50) {
                return lastAdjustedSpawnRate;
            }
            lastDecreaseTime = currentTime;
        }
        // Check if the spawn rate is being increased
        else if (newSpawnRate > lastAdjustedSpawnRate) {
            if ((currentTime - lastIncreaseTime) < INCREASE_DELAY_TICKS * 50) {
                return lastAdjustedSpawnRate;
            }
            lastIncreaseTime = currentTime;
        }

        // Log the adjustment if it changed
        if (newSpawnRate != lastAdjustedSpawnRate) {
            Bukkit.getLogger().info("[TGF] Adjusting spawn rate to " + newSpawnRate + "% due to current MSPT: " + currentMSPT);
            lastAdjustedSpawnRate = newSpawnRate; // Update the last adjusted spawn rate
        }

        // If the server is running very efficiently and the spawn rate is low, reset it.
        if (currentMSPT <= 20 && newSpawnRate < 100) {
            resetSpawnRate();
            newSpawnRate = 100;  // Ensure we return the reset spawn rate
            lastAdjustedSpawnRate = 100; // Keep our static value consistent
        }

        return newSpawnRate;
    }
    public static void resetSpawnRate() {
        lastAdjustedSpawnRate = 100;
        Bukkit.getLogger().info("[TGF] Spawn rate has been reset to 100%");
    }

}