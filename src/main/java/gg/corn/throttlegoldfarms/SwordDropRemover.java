package gg.corn.throttlegoldfarms;

import org.bukkit.Material;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;


public class SwordDropRemover implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof PigZombie)) {
            return;
        }

        PigZombie pigZombie = (PigZombie) event.getEntity();

        // Only target PigZombies that were spawned from Nether Portals
        if (!pigZombie.hasMetadata("spawnedFromNetherPortal")) {
            return;
        }

        // Remove only the gold sword drops
        Iterator<ItemStack> dropIterator = event.getDrops().iterator();
        while (dropIterator.hasNext()) {
            ItemStack drop = dropIterator.next();
            if (drop.getType() == Material.GOLDEN_SWORD) {
                dropIterator.remove();
            }
        }
    }

}
