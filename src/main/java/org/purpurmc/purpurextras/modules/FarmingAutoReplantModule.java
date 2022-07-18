package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;


import java.util.Arrays;
import java.util.List;


import static org.bukkit.Material.*;
import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

public class FarmingAutoReplantModule implements PurpurExtrasModule, Listener {

    protected FarmingAutoReplantModule() {}

    private final List<Material> possibleFarmables = Arrays.asList(CARROTS, COCOA, NETHER_WART, POTATOES, WHEAT, BEETROOTS, MELON_STEM, PUMPKIN_STEM);

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        registerPermission("purpurextras.autoreplant", "Allows a player to right-click a full-grown crop to harvest it and auto-replant", PermissionDefault.OP);
        //TODO: add configuration options to choose which farmable items can be auto-replpanted
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.right-click-to-auto-replant.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void rightClickFarmables(PlayerInteractEvent event) {
        if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (event.getAction().isLeftClick()) return;
        if (event.getClickedBlock() == null) return;
        //TODO: change check the configured list, not the hardcoded one
        if (!possibleFarmables.contains(event.getClickedBlock().getType())) return;
        if (!event.getPlayer().hasPermission("purpurextras.autoreplant")) return;
        Player player = event.getPlayer();
        Block clickedSpot = event.getClickedBlock();
        Material clickedMaterial = clickedSpot.getType();
        Ageable clickedCrop = (Ageable) clickedSpot.getBlockData();
        ItemStack itemUsed = player.getInventory().getItemInMainHand();
        BlockFace facing = null;
        if (clickedCrop.getMaximumAge() != clickedCrop.getAge()) return;
        if (clickedMaterial.equals(COCOA)){
            Cocoa clickedCocoa = (Cocoa) event.getClickedBlock().getBlockData();
            facing = clickedCocoa.getFacing();
            Cocoa cocoaData = (Cocoa) Bukkit.createBlockData(COCOA);
            event.setCancelled(true);
            event.getClickedBlock().breakNaturally(itemUsed);
            cocoaData.setFacing(facing);
            clickedSpot.setBlockData(cocoaData);
            return;
        }
        event.setCancelled(true);
        event.getClickedBlock().breakNaturally(itemUsed);
        event.getClickedBlock().setType(clickedMaterial);
    }
}
