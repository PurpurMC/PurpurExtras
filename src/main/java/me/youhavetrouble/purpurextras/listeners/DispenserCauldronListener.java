package me.youhavetrouble.purpurextras.listeners;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DispenserCauldronListener implements Listener {
    //I know there's already a dispenser listener that listens for this event but I couldn't figure out how on earth to put it in without messing it up :D
    BlockData waterCauldron = Bukkit.createBlockData(Material.WATER_CAULDRON, blockData -> {
        Levelled cauldron = (Levelled) blockData;
        cauldron.setLevel(3);
    });
    BlockData powderSnowCauldron = Bukkit.createBlockData(Material.POWDER_SNOW_CAULDRON, blockData -> {
        Levelled cauldron = (Levelled) blockData;
        cauldron.setLevel(3);
    });
    BlockData lavaCauldron = Bukkit.createBlockData(Material.LAVA_CAULDRON);
    List<Material> cauldronTypes = Arrays.asList(Material.CAULDRON, Material.LAVA_CAULDRON, Material.WATER_CAULDRON, Material.POWDER_SNOW_CAULDRON);
    List<Material> bucketTypes = Arrays.asList(Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.POWDER_SNOW_BUCKET);

    @EventHandler
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        ItemStack dispensedItems = event.getItemStack();
        Material dispensedItemType = dispensedItems.getType();
        Material blockType = block.getType();
        Levelled cauldronLevel;
        if(!cauldronTypes.contains(blockType)) return;
        if(!bucketTypes.contains(dispensedItems.getType())) return;
        if(block.getType().equals(Material.CAULDRON)){
            if(dispensedItemType.equals(Material.BUCKET)) return;
            if(dispensedItemType.equals(Material.LAVA_BUCKET)){
                emptyCauldronHandler(block, lavaCauldron, dispensedItems, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA);
                event.setCancelled(true);
                return;
            }
            if (dispensedItemType.equals(Material.WATER_BUCKET)){
                emptyCauldronHandler(block, waterCauldron, dispensedItems, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY);
                event.setCancelled(true);
                return;
            }
            if(dispensedItemType.equals(Material.POWDER_SNOW_BUCKET)){
                emptyCauldronHandler(block, powderSnowCauldron, dispensedItems, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW);
                event.setCancelled(true);
                return;
            }
        }
        if(!dispensedItemType.equals(Material.BUCKET)) return;
        if(block.getType().equals(Material.LAVA_CAULDRON)){
            fullCauldronHandler(block, dispensedItems, blockDispenser, Material.LAVA_BUCKET, Sound.ITEM_BUCKET_FILL_LAVA);
            event.setCancelled(true);
            return;
        }
        if(block.getType().equals(Material.WATER_CAULDRON)){
            cauldronLevel = (Levelled) block.getBlockData();
            if (cauldronLevel.getLevel() < 3) return;
            fullCauldronHandler(block, dispensedItems, blockDispenser, Material.WATER_BUCKET, Sound.ITEM_BUCKET_FILL);
            event.setCancelled(true);
            return;
        }
        if(block.getType().equals(Material.POWDER_SNOW_CAULDRON)){
            cauldronLevel = (Levelled) block.getBlockData();
            if (cauldronLevel.getLevel() < 3) return;
            fullCauldronHandler(block, dispensedItems, blockDispenser, Material.POWDER_SNOW_BUCKET, Sound.ITEM_BUCKET_FILL_POWDER_SNOW);
            event.setCancelled(true);
        }
    }

    public void emptyCauldronHandler(Block cauldron,
                                     BlockData cauldronType,
                                     ItemStack dispensingItem,
                                     Location dispLocation,
                                     Sound uniqueSound){
        cauldron.setBlockData(cauldronType);
        dispensingItem.setType(Material.BUCKET);
        dispLocation.getWorld().playSound(dispLocation, Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
        dispLocation.getWorld().playSound(dispLocation, uniqueSound, SoundCategory.BLOCKS, 1, 1);
    }

    public void fullCauldronHandler(Block cauldron,
                                    ItemStack items,
                                    org.bukkit.block.Dispenser dispenserBlock,
                                    Material newItem,
                                    Sound uniqueSound){
        cauldron.setType(Material.CAULDRON);
        ItemStack newItemDrop = new ItemStack(newItem);
        int emptySlot = dispenserBlock.getInventory().firstEmpty();
        if(items.getAmount() > 1){
            items.setAmount(items.getAmount() - 1);
            if(emptySlot == -1){
                cauldron.getWorld().dropItem(cauldron.getLocation(), newItemDrop);
            } else {
                dispenserBlock.getInventory().setItem(emptySlot, newItemDrop);
            }
        } else {
            items.setType(newItem);
        }
        dispenserBlock.getWorld().playSound(dispenserBlock.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
        dispenserBlock.getWorld().playSound(dispenserBlock.getLocation(), uniqueSound, SoundCategory.BLOCKS, 1, 1);
    }
}


