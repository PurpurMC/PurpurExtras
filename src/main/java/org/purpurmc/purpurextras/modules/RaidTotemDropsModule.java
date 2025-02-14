package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;

/**
 * Modifies the drop rate of totems from Evokers spawned in a raid
 */
public class RaidTotemDropsModule implements PurpurExtrasModule, Listener {

    private final SplittableRandom random;
    private final double dropChance;
    private final Map<UUID, Raider> raiders = new HashMap<>();

    private final ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

    protected RaidTotemDropsModule() {
        dropChance = PurpurExtras.getPurpurConfig().getDouble("settings.raid-totem-drops.chance", 0);
        random = new SplittableRandom();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onRaidSpawn(RaidSpawnWaveEvent event) {
        event.getRaiders().forEach(r -> raiders.put(r.getUniqueId(), r));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onRaidDeath(EntityDeathEvent event) {
        if (raiders.get(event.getEntity().getUniqueId()) == null) return;
        raiders.remove(event.getEntity().getUniqueId());
        if (event.getEntityType() != EntityType.EVOKER) return;
        boolean totemShouldDrop = random.nextFloat() < dropChance;
        event.getDrops().stream().filter(i -> i.getType() == Material.TOTEM_OF_UNDYING).findFirst().ifPresentOrElse(i -> {
            if (!totemShouldDrop) event.getDrops().remove(i);
        }, () -> {
            if (totemShouldDrop) event.getDrops().add(totem);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onRaidEnd(RaidStopEvent event) {
        event.getRaid().getRaiders().stream().map(Entity::getUniqueId).forEach(raiders::remove);
    }

    @Override
    public void enable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, PurpurExtras.getInstance());
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.raid-totem-drops.enabled", false);
    }
}
