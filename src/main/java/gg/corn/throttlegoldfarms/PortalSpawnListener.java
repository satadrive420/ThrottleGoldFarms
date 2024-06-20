package gg.corn.throttlegoldfarms;



import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PigZombie;
import static org.bukkit.Bukkit.getServer;


public class PortalSpawnListener implements Listener {

    private final ThrottleGoldFarms plugin;

    public PortalSpawnListener(ThrottleGoldFarms plugin) {
        this.plugin = plugin;
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
                } else {
                    // Set the HandDropChance to 0 if the entity was not cancelled
                    pigZombie.getEquipment().setItemInMainHandDropChance(0.0f);
                    if (verbose) {
                        Bukkit.getLogger().info("[TGF] Set HandDropChance to 0 for a PigZombie.");
                    }
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
        double lastAdjustedSpawnRate = plugin.getConfig().getDouble("baseSpawnRate");
        double newSpawnRate;

        if (currentMSPT <= minMSPT) {
            newSpawnRate = baseSpawnRate;
        } else if (currentMSPT >= maxMSPT) {
            newSpawnRate = 0;
        } else {
            newSpawnRate = baseSpawnRate * (1 - (currentMSPT - minMSPT) / (maxMSPT - minMSPT));
        }

        // Check if the delay period has passed
        // Check if the spawn rate is being decreased
        if (newSpawnRate < lastAdjustedSpawnRate) {
            if ((System.currentTimeMillis() - lastDecreaseTime) < DECREASE_DELAY_TICKS * 50) {
                return lastAdjustedSpawnRate;
            }
            lastDecreaseTime = System.currentTimeMillis();
        }
        // Check if the spawn rate is being increased
        else if (newSpawnRate > lastAdjustedSpawnRate) {
            if ((System.currentTimeMillis() - lastIncreaseTime) < INCREASE_DELAY_TICKS * 50) {
                return lastAdjustedSpawnRate;
            }
            lastIncreaseTime = System.currentTimeMillis();
        }


        // Log the adjustment
        if (newSpawnRate != baseSpawnRate){
            Bukkit.getLogger().info("[TGF] Adjusting spawn rate to " + newSpawnRate + "% due to current MSPT: " + currentMSPT);
            lastAdjustedSpawnRate = newSpawnRate; // Update the last adjusted spawn rate
        }

        return newSpawnRate;
    }


}