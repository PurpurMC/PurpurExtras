package me.youhavetrouble.purpurextras.listeners;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DispenserCauldronListener implements Listener {
    //I know there's already a dispenser listener that listens for this event but I couldn't figure out how on earth to put it in without messing it up :D
    List<Material> cauldronTypes = Arrays.asList(Material.CAULDRON, Material.LAVA_CAULDRON, Material.WATER_CAULDRON, Material.POWDER_SNOW_CAULDRON);
    List<Material> bucketTypes = Arrays.asList(Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.POWDER_SNOW_BUCKET);
    SoundCategory blockSound = SoundCategory.BLOCKS;
    Sound dispenseSound = Sound.BLOCK_DISPENSER_DISPENSE;

    @EventHandler
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        Inventory dispenserInventory = blockDispenser.getInventory();
        int emptySlot = dispenserInventory.firstEmpty();
        Levelled cauldronBlock;
        BlockData waterCauldron = Bukkit.createBlockData(Material.WATER_CAULDRON, blockData -> {
            Levelled cauldron = (Levelled) blockData;
            cauldron.setLevel(3);
        });
        BlockData powderSnowCauldron = Bukkit.createBlockData(Material.POWDER_SNOW_CAULDRON, blockData -> {
            Levelled cauldron = (Levelled) blockData;
            cauldron.setLevel(3);
        });
        ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET);
        ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
        ItemStack powderSnowBucket = new ItemStack(Material.POWDER_SNOW_BUCKET);
        Location dispenserLocation = blockDispenser.getLocation();
        Location cauldronLocation = block.getLocation();
        Sound lavaDispense = Sound.ITEM_BUCKET_EMPTY_LAVA;
        Sound lavaEmpty = Sound.ITEM_BUCKET_FILL_LAVA;
        Sound waterDispense = Sound.ITEM_BUCKET_EMPTY;
        Sound waterEmpty = Sound.ITEM_BUCKET_FILL;
        Sound powderSnowDispense = Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW;
        Sound powderSnowEmpty = Sound.ITEM_BUCKET_FILL_POWDER_SNOW;
        Material dispensedItemMaterial = event.getItemStack().getType();
        ItemStack dispensedItem = event.getItemStack();
        Material material = block.getType();

        if(!(cauldronTypes.contains(block.getType()))) return;
        if(!(bucketTypes.contains(dispensedItemMaterial))) return;
        switch (material){
            case CAULDRON:
                if(dispensedItemMaterial.equals(Material.BUCKET)){
                    return;
                } else {
                    switch (dispensedItemMaterial) {
                        case LAVA_BUCKET -> {
                            block.setType(Material.LAVA_CAULDRON);
                            dispensedItem.setType(Material.BUCKET);
                            playSounds(dispenserLocation, lavaDispense);
                            event.setCancelled(true);
                            return;
                        }
                        case WATER_BUCKET -> {
                            block.setBlockData(waterCauldron);
                            dispensedItem.setType(Material.BUCKET);
                            playSounds(dispenserLocation, waterDispense);
                            event.setCancelled(true);
                            return;
                        }
                        case POWDER_SNOW_BUCKET -> {
                            block.setBlockData(powderSnowCauldron);
                            dispensedItem.setType(Material.BUCKET);
                            playSounds(dispenserLocation, powderSnowDispense);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

            case LAVA_CAULDRON:
                if(!(dispensedItemMaterial.equals(Material.BUCKET))) return;
                block.setType(Material.CAULDRON);
                if(dispensedItem.getAmount() > 1) {
                    dispensedItem.setAmount(dispensedItem.getAmount() - 1);
                    if (emptySlot == -1) {
                        block.getWorld().dropItem(cauldronLocation, lavaBucket);
                        playSounds(dispenserLocation, lavaEmpty);
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(true);
                    playSounds(dispenserLocation, lavaEmpty);
                    blockDispenser.getInventory().setItem(emptySlot, lavaBucket);
                    return;
                }
                event.setCancelled(true);
                playSounds(dispenserLocation, lavaEmpty);
                dispensedItem.setType(Material.LAVA_BUCKET);
                return;
            case WATER_CAULDRON:
                if(!(dispensedItemMaterial.equals(Material.BUCKET))) return;
                cauldronBlock = (Levelled) block.getBlockData();
                if(cauldronBlock.getLevel() < 3) {
                    event.setCancelled(true);
                    return;
                }
                block.setType(Material.CAULDRON);
                if(dispensedItem.getAmount() > 1){
                    dispensedItem.setAmount(dispensedItem.getAmount() - 1);
                    if(emptySlot == -1){
                        block.getWorld().dropItem(cauldronLocation, waterBucket);
                        playSounds(dispenserLocation, waterEmpty);
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(true);
                    playSounds(dispenserLocation, waterEmpty);
                    blockDispenser.getInventory().setItem(emptySlot, waterBucket);
                    return;
                }
                event.setCancelled(true);
                playSounds(dispenserLocation, waterEmpty);
                dispensedItem.setType(Material.WATER_BUCKET);
                return;
            case POWDER_SNOW_CAULDRON:
                if(!(dispensedItemMaterial.equals(Material.BUCKET))) return;
                cauldronBlock = (Levelled) block.getBlockData();
                if(cauldronBlock.getLevel() < 3) {
                    event.setCancelled(true);
                    return;
                }
                block.setType(Material.CAULDRON);
                if(dispensedItem.getAmount() > 1){
                    dispensedItem.setAmount(dispensedItem.getAmount() - 1);
                    if(emptySlot == -1){
                        block.getWorld().dropItem(cauldronLocation, powderSnowBucket);
                        playSounds(dispenserLocation, powderSnowEmpty);
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(true);
                    playSounds(dispenserLocation, powderSnowEmpty);
                    blockDispenser.getInventory().setItem(emptySlot, powderSnowBucket);
                    return;
                }
                event.setCancelled(true);
                playSounds(dispenserLocation, powderSnowEmpty);
                dispensedItem.setType(Material.POWDER_SNOW_BUCKET);
        }
    }
    public void playSounds(Location dLoc, Sound actionSound){
        dLoc.getWorld().playSound(dLoc, dispenseSound, blockSound, 1, 1);
        dLoc.getWorld().playSound(dLoc, actionSound, blockSound, 1, 1);
    }
}

