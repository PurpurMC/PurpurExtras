package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.BlockTypeKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.util.ItemStackUtil;

import java.util.Map;

/**
 * Dispenser modifications
 *
 * **break-blocks**
 * If a tool category is enabled, that tool dispensed from dispenser will destroy the block in front of it.
 * It will only destroy blocks that tool can destroy and it will destroy them like that tool was used on it,
 * so wooden pickaxe will destroy diamond ore, but will not drop any items.
 *
 * **shears-shear-pumpkin**
 * If enabled, when shears are dispensed and there's a pumpkin in front of a dispenser, shears will be used, making carved pumpkin.
 *
 * **interact-with-cauldron**
 * If enabled, will allow dispensers fill and empty cauldrons.
 *
 * **put-discs-in-jukebox**
 * If enabled, dispensers will be able to insert into or swap music discs in jukeboxes.
 */
public class DispenserBlocksModule implements PurpurExtrasModule, Listener {

    private static final MaterialSetTag CAULDRON_BUCKETS = new MaterialSetTag(new NamespacedKey(PurpurExtras.getInstance(), "cauldron_buckets"))
            .add(Material.BUCKET)
            .add(Material.WATER_BUCKET)
            .add(Material.LAVA_BUCKET)
            .add(Material.POWDER_SNOW_BUCKET);

    BlockData waterCauldron = Bukkit.createBlockData(Material.WATER_CAULDRON, blockData -> {
        Levelled cauldron = (Levelled) blockData;
        cauldron.setLevel(3);
    });
    BlockData powderSnowCauldron = Bukkit.createBlockData(Material.POWDER_SNOW_CAULDRON, blockData -> {
        Levelled cauldron = (Levelled) blockData;
        cauldron.setLevel(3);
    });
    BlockData lavaCauldron = Bukkit.createBlockData(Material.LAVA_CAULDRON);

    private final boolean breakBlockPickaxe, breakBlockShovel, breakBlockHoe, breakBlockAxe, breakBlockShears,
            shearPumpkin, activateJukebox, interactWithCauldron;

    protected DispenserBlocksModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        breakBlockPickaxe = config.getBoolean("settings.dispenser.break-blocks.pickaxe", false);
        breakBlockShovel = config.getBoolean("settings.dispenser.break-blocks.shovel", false);
        breakBlockHoe = config.getBoolean("settings.dispenser.break-blocks.hoe", false);
        breakBlockAxe = config.getBoolean("settings.dispenser.break-blocks.axe", false);
        breakBlockShears = config.getBoolean("settings.dispenser.break-blocks.shears", false);
        shearPumpkin = config.getBoolean("settings.dispenser.shears-shear-pumpkin", false);
        activateJukebox = config.getBoolean("settings.dispenser.puts-discs-in-jukebox", false);
        interactWithCauldron = config.getBoolean("settings.dispenser.interact-with-cauldron", false);

    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return anyTrue(
                breakBlockAxe,
                breakBlockHoe,
                breakBlockPickaxe,
                breakBlockShovel,
                breakBlockShears,
                shearPumpkin,
                activateJukebox,
                interactWithCauldron
        );
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        ItemStack item = event.getItemStack();

        if (breakBlockPickaxe && MaterialTags.PICKAXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockAxe && MaterialTags.AXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockShovel && MaterialTags.SHOVELS.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockHoe && MaterialTags.HOES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockShears && Material.SHEARS.equals(item.getType())) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        // Shear pumpkin
        if (shearPumpkin
                && item.getType().equals(Material.SHEARS)
                && block.getType().equals(Material.PUMPKIN)
        ) {
            Inventory inventory = blockDispenser.getInventory();
            damageItem(item, inventory);
            event.setCancelled(true);
            block.setType(Material.CARVED_PUMPKIN);
            return;
        }

        // Swap records in jukebox
        if (activateJukebox
                && MaterialTags.MUSIC_DISCS.isTagged(item)
                && block.getType().equals(Material.JUKEBOX)
        ) {
            event.setCancelled(true);
            Jukebox jukebox = (Jukebox) block.getState(false);
            jukebox.eject();
            ItemStack record = consumeItem(item);
            jukebox.setRecord(record);
            jukebox.update();
            return;
        }

        //Dispense liquid in and from cauldrons
        if (interactWithCauldron && Tag.CAULDRONS.isTagged(block.getType()) && CAULDRON_BUCKETS.isTagged(item)){
            Material dispensedItemType = item.getType();
            Levelled cauldronLevel;
            if(block.getType().equals(Material.CAULDRON)){
                switch(dispensedItemType){
                    case BUCKET -> { return; }
                    case LAVA_BUCKET -> {
                        emptyCauldronHandler(block, lavaCauldron, item, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA);
                        event.setCancelled(true);
                        return;
                    }
                    case WATER_BUCKET -> {
                        emptyCauldronHandler(block, waterCauldron, item, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY);
                        event.setCancelled(true);
                        return;
                    }
                    case POWDER_SNOW_BUCKET -> {
                        emptyCauldronHandler(block, powderSnowCauldron, item, blockDispenser.getLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if(!dispensedItemType.equals(Material.BUCKET)) return;
            switch (block.getType()) {
                case LAVA_CAULDRON -> {
                    fullCauldronHandler(block, item, blockDispenser, Material.LAVA_BUCKET, Sound.ITEM_BUCKET_FILL_LAVA);
                    event.setCancelled(true);
                }
                case WATER_CAULDRON -> {
                    cauldronLevel = (Levelled) block.getBlockData();
                    if (cauldronLevel.getLevel() < 3) return;
                    fullCauldronHandler(block, item, blockDispenser, Material.WATER_BUCKET, Sound.ITEM_BUCKET_FILL);
                    event.setCancelled(true);
                }
                case POWDER_SNOW_CAULDRON -> {
                    cauldronLevel = (Levelled) block.getBlockData();
                    if (cauldronLevel.getLevel() < 3) return;
                    fullCauldronHandler(block, item, blockDispenser, Material.POWDER_SNOW_BUCKET, Sound.ITEM_BUCKET_FILL_POWDER_SNOW);
                    event.setCancelled(true);
                }
            }
        }
    }

    private void damageItem(ItemStack itemStack, Inventory inventory) {
        int slot = inventory.first(itemStack);
        if (slot == -1) return;
        ItemStack item = inventory.getItem(slot);
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) return;
        ItemStackUtil.damage(item, 1, false);
    }

    private ItemStack consumeItem(ItemStack itemStack) {
        ItemStack consumed = itemStack.clone();
        consumed.setAmount(1);
        itemStack.setAmount(itemStack.getAmount()-1);
        return consumed;
    }

    private boolean tryBreakBlock(ItemStack itemStack, org.bukkit.block.Dispenser dispenser, Block block) {
        if (block.getDestroySpeed(itemStack, false) <= 1.0f) return false;
        Inventory inventory = dispenser.getInventory();
        damageItem(itemStack, inventory);
        return block.breakNaturally(itemStack, true);
    }

    private boolean anyTrue(boolean... booleans) {
        for (boolean b : booleans) {
            if (b) return true;
        }
        return false;
    }

    private void emptyCauldronHandler(Block cauldron, BlockData cauldronType, ItemStack dispensingItem,
                                      Location dispLocation, Sound uniqueSound){
        cauldron.setBlockData(cauldronType);
        dispensingItem.setType(Material.BUCKET);
        dispLocation.getWorld().playSound(dispLocation, Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
        dispLocation.getWorld().playSound(dispLocation, uniqueSound, SoundCategory.BLOCKS, 1, 1);
    }

    private void fullCauldronHandler(Block cauldron,
                                    ItemStack items,
                                    org.bukkit.block.Dispenser dispenserBlock,
                                    Material newItem,
                                    Sound uniqueSound){
        cauldron.setType(Material.CAULDRON);
        Inventory inv = dispenserBlock.getInventory();
        ItemStack newItemDrop = new ItemStack(newItem);

        //handling for stacked buckets
        if(items.getAmount() > 1){
            items.setAmount(items.getAmount() - 1);
            Map<Integer, ItemStack> map = inv.addItem(newItemDrop);
            if(!map.isEmpty()) {
                cauldron.getWorld().dropItem(cauldron.getLocation(), newItemDrop);
            }
        } else {
            items.setType(newItem);
        }
        dispenserBlock.getWorld().playSound(dispenserBlock.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
        dispenserBlock.getWorld().playSound(dispenserBlock.getLocation(), uniqueSound, SoundCategory.BLOCKS, 1, 1);
    }
}
