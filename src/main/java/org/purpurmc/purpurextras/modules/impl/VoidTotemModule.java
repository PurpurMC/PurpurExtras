package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * If enabled, totem of undying will save players from death in the void and will
 * teleport them to the last place their feet touched the ground.
 * If for any reason that position is not found, they will be teleported to world spawn.
 */
@ModuleInfo(name = "Totem Void Deaths", description = "Totems will prevent dying in the void!")
public class VoidTotemModule extends PurpurExtrasModule {

    private final Collection<PotionEffect> totemEffects = new ArrayList<>();
    private final HashMap<UUID, Location> lastGroundedLocations = new HashMap<>();

    public VoidTotemModule() {
        totemEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 20*45, 1));
        totemEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*40, 0));
        totemEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 20*5, 1));
    }

    @Override
    public String getConfigPath() {
        return "settings.totem.work-on-void-death";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        Location location = event.getTo().clone();
        if (location.subtract(0, 0.05, 0).getBlock().getType().isAir()) return;
        lastGroundedLocations.put(event.getPlayer().getUniqueId(), event.getTo().toCenterLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastGroundedLocations.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathInVoid(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;
        Location location = player.getLocation();
        if (location.getY() > location.getWorld().getMinHeight()) return;

        if (player.getHealth() - event.getFinalDamage() > 0) return;

        if (!player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)
                && !player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)
        ) return;

        event.setCancelled(true);

        Location safeLocation = lastGroundedLocations.getOrDefault(player.getUniqueId(), location.getWorld().getSpawnLocation());
        player.teleportAsync(safeLocation).thenRun(() -> useTotem(player));
    }

    private void useTotem(Player player) {
        ItemStack totem = null;
        if (player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
            totem = player.getInventory().getItemInMainHand();
        } else if (player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
            totem = player.getInventory().getItemInOffHand();
        }
        if (totem == null) return;
        totem.subtract();
        player.setFallDistance(0);
        player.setHealth(1);
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffects(this.totemEffects);
    }
}
