package gg.corn.throttlegoldfarms;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;


public class SwordDropRemover implements Listener {

    private static ThrottleGoldFarms plugin;
    private static final String META_KEY = "spawnedFromPortal";

    public SwordDropRemover(ThrottleGoldFarms plugin) {
        SwordDropRemover.plugin = plugin;
    }

    // Tag any zombified piglin spawned from a nether portal
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIFIED_PIGLIN
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL) {
            event.getEntity().setMetadata(META_KEY, new FixedMetadataValue(plugin, true));
        }
    }

    // On death, if tagged, remove golden sword from drops
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ZOMBIFIED_PIGLIN) return;

        Entity piglin = event.getEntity();
        if (piglin.hasMetadata(META_KEY)) {
            event.getDrops().removeIf(stack -> stack.getType() == Material.GOLDEN_SWORD);
        }
    }

}
