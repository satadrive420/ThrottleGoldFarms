package gg.corn.throttlegoldfarms;



import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PigZombie;
import static org.bukkit.Bukkit.getServer;


public class PortalSpawnListener implements Listener {

    private final ThrottleGoldFarms plugin;
    private long lastAdjustmentTime = 0; // Timestamp of the last adjustment
    public static double lastAdjustedSpawnRate; // Stores the last adjusted spawn rate
    private static final long ADJUSTMENT_DELAY_TICKS = 600; // 600 ticks delay

    public PortalSpawnListener(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        boolean verbose = plugin.getConfig().getBoolean("verbose-logging");

        if (event.getEntity() instanceof PigZombie) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL) {
                double currentMSPT = getCurrentMSPT();
                double adjustedSpawnRate = calculateSpawnRate(currentMSPT);

                if (Math.random() * 100 > adjustedSpawnRate) {
                    if(verbose){
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

        // Check if the delay period has passed
        if ((System.currentTimeMillis() - lastAdjustmentTime) < ADJUSTMENT_DELAY_TICKS * 50) {
            // Not enough time has passed, return the last adjusted spawn rate
            return lastAdjustedSpawnRate;
        }

        boolean verbose = plugin.getConfig().getBoolean("verbose-logging");
        double minMSPT = plugin.getConfig().getDouble("minMSPT");
        double maxMSPT = plugin.getConfig().getDouble("maxMSPT");
        double baseSpawnRate = plugin.getConfig().getDouble("baseSpawnRate");

        double newSpawnRate;

        if (currentMSPT <= minMSPT) {
            newSpawnRate = baseSpawnRate;
        } else if (currentMSPT >= maxMSPT) {
            newSpawnRate = 0;
        } else {
            newSpawnRate = baseSpawnRate * (1 - (currentMSPT - minMSPT) / (maxMSPT - minMSPT));
        }

        // Log the adjustment
        if (newSpawnRate != baseSpawnRate){
            if(verbose) {
                Bukkit.getLogger().info("[TGF] Adjusting spawn rate to " + newSpawnRate + "% due to current MSPT: " + currentMSPT);
            }
            lastAdjustmentTime = System.currentTimeMillis(); // Update the last adjustment time
            lastAdjustedSpawnRate = newSpawnRate; // Update the last adjusted spawn rate
        }

        return newSpawnRate;
    }


}