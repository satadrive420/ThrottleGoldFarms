package gg.corn.throttlegoldfarms;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.PigZombie;

import java.util.List;
import java.util.stream.Collectors;

public class ReinforcementSpawnListener implements Listener {

    // Radius to check for a "caller" pigzombie that was spawned from a Nether Portal
    private static final double CHECK_RADIUS = 16.0;

    @EventHandler
    public void onReinforcementSpawn(CreatureSpawnEvent event) {
        // Only handle reinforcement spawns for PigZombies
        if (!(event.getEntity() instanceof PigZombie)) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS) {
            return;
        }

        PigZombie spawnedReinforcement = (PigZombie) event.getEntity();
        // Look for any nearby PigZombies with the "spawnedFromNetherPortal" metadata flag.
        List<PigZombie> nearbyNetherPortalPigs = spawnedReinforcement.getWorld()
                .getEntitiesByClass(PigZombie.class)
                .stream()
                .filter(pig -> pig.hasMetadata("spawnedFromNetherPortal"))
                .filter(pig -> pig.getLocation().distance(spawnedReinforcement.getLocation()) <= CHECK_RADIUS)
                .collect(Collectors.toList());

        if (!nearbyNetherPortalPigs.isEmpty()) {
            // Cancel the reinforcement spawn if any such caller is nearby.
            event.setCancelled(true);
            Bukkit.getLogger().info("[TGF] Cancelled reinforcement spawn due to nearby Nether Portal spawned PigZombie.");
        }
    }
}
