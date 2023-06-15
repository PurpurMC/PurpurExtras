package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RaidTotemDropsModule implements PurpurExtrasModule, Listener {

    private final SplittableRandom random;
    private final int dropChance;
    private final Map<UUID, Raider> raiders = new ConcurrentHashMap<>();

    protected RaidTotemDropsModule() {
        dropChance = PurpurExtras.getPurpurConfig().getInt("settings.raid-totem-drops.chance", 0);
        random = new SplittableRandom();
    }

    @EventHandler
    public void onRaidSpawn(RaidSpawnWaveEvent ev) {
        ev.getRaiders().forEach(r -> raiders.put(r.getUniqueId(), r));
    }

    @EventHandler
    public void onRaidDeath(EntityDeathEvent ev) {
        if(raiders.get(ev.getEntity().getUniqueId()) == null) return;
        raiders.remove(ev.getEntity().getUniqueId());
        if(ev.getEntityType() != EntityType.EVOKER) return;
        boolean totem = dropChance >= 100 || random.nextInt(1, 101) <= dropChance;
        ev.getDrops().stream().filter(i -> i.getType() == Material.TOTEM_OF_UNDYING).findFirst().ifPresentOrElse(i -> {
            if(!totem)
                ev.getDrops().remove(i);
        }, () -> {
            if(totem)
                ev.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
        });
    }

    @EventHandler
    public void onRaidEnd(RaidStopEvent ev) {
        ev.getRaid().getRaiders().stream().map(Entity::getUniqueId).forEach(raiders::remove);
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
