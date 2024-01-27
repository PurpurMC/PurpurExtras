package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
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

import java.util.*;
import java.util.logging.Logger;

import static com.destroystokyo.paper.MaterialTags.*;

/**
 * Dispenser modifications
 *
 * **break-blocks**
 * If a tool category is enabled, that tool dispensed from dispenser will destroy the block in front of it.
 * It will only destroy blocks that tool can destroy, and it will destroy them like that tool was used on it,
 * so wooden pickaxe will destroy diamond ore, but will not drop any items.
 *
 * **interact-blocks**
 * If enabled, a tool dispensed from a dispenser will "interact" (AKA "right-click" or "use") with a block using that
 * tool. For example, a hoe "interacting" with a dirt block turns it into farmland, shears with pumpkin becomes a
 * jack-o'-lantern, etc. Only blocks specified in the config are able to be interacted with.
 *
 * **shears-shear-pumpkin**
 * If enabled, when shears are dispensed and there's a pumpkin in front of a dispenser, shears will be used, making carved pumpkin.
 * This does the same as adding PUMPKIN to settings.dispenser.interact-blocks.shears, and is present for
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
            interactBlockEnable, shearPumpkin, activateJukebox, interactWithCauldron;

    private final HashSet<Material> interactBlockShovel = new HashSet<>();
    private final HashSet<Material> interactBlockHoe = new HashSet<>();
    private final HashSet<Material> interactBlockAxe = new HashSet<>();

    protected DispenserBlocksModule() {
        List<String> defaults = new ArrayList<>();
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        Logger logger = PurpurExtras.getInstance().getLogger();

        breakBlockPickaxe = config.getBoolean("settings.dispenser.break-blocks.pickaxe", false);
        breakBlockShovel = config.getBoolean("settings.dispenser.break-blocks.shovel", false);
        breakBlockHoe = config.getBoolean("settings.dispenser.break-blocks.hoe", false);
        breakBlockAxe = config.getBoolean("settings.dispenser.break-blocks.axe", false);
        breakBlockShears = config.getBoolean("settings.dispenser.break-blocks.shears", false);
        interactBlockEnable = config.getBoolean("settings.dispenser.interact-blocks.enable", false);
        shearPumpkin = config.getBoolean("settings.dispenser.shears-shear-pumpkin", false);
        activateJukebox = config.getBoolean("settings.dispenser.puts-discs-in-jukebox", false);
        interactWithCauldron = config.getBoolean("settings.dispenser.interact-with-cauldron", false);

        config.getList("settings.dispenser.interact-blocks.shovel", defaults).forEach((string) ->{
            Material material = Material.getMaterial(string.toUpperCase(Locale.ENGLISH));
            if (material == null) {
                logger.warning(string + " is not a valid block material.");
                return;
            }
            interactBlockShovel.add(material);
        });
        config.getList("settings.dispenser.interact-blocks.hoe", defaults).forEach((string) ->{
            Material material = Material.getMaterial(string.toUpperCase(Locale.ENGLISH));
            if (material == null) {
                logger.warning(string + " is not a valid block material.");
                return;
            }
            interactBlockHoe.add(material);
        });
        config.getList("settings.dispenser.interact-blocks.axe", defaults).forEach((string) ->{
            Material material = Material.getMaterial(string.toUpperCase(Locale.ENGLISH));
            if (material == null) {
                logger.warning(string + " is not a valid block material.");
                return;
            }
            interactBlockAxe.add(material);
        });
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
                interactBlockEnable,
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

        // Block Interactions/Breaking

        if (breakBlockPickaxe && MaterialTags.PICKAXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        if (interactBlockEnable && AXES.isTagged(item) && interactBlockAxe.contains(block.getType()) && isAxeInteraction(block.getType())) {
            event.setCancelled(true);
            block.setType(Objects.requireNonNull(getInteraction(AXES, block.getType())));
            damageItem(item, blockDispenser.getInventory());
            return;
        } else if (breakBlockAxe && MaterialTags.AXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        if (interactBlockEnable && SHOVELS.isTagged(item) && interactBlockShovel.contains(block.getType()) && isShovelInteraction(block.getType())) {
            event.setCancelled(true);
            if (Objects.requireNonNull(getInteraction(SHOVELS, block.getType())).equals(Material.DIRT_PATH)) {
                if (block.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
                    block.setType(Material.DIRT_PATH);
                    damageItem(item, blockDispenser.getInventory());
                }
            } else {
                Lightable campfire = (Lightable) block;
                campfire.setLit(false);
                damageItem(item, blockDispenser.getInventory());
            }
            return;
        } else if (breakBlockShovel && MaterialTags.SHOVELS.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        if (interactBlockEnable && HOES.isTagged(item) && interactBlockHoe.contains(block.getType()) && isHoeInteraction(block.getType())) {
            event.setCancelled(true);
            if (Objects.requireNonNull(getInteraction(HOES, block.getType())).equals(Material.FARMLAND)) {
                if (block.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
                    block.setType(Material.FARMLAND);
                    damageItem(item, blockDispenser.getInventory());
                }
            } else {
                block.setType(Material.DIRT);
                damageItem(item, blockDispenser.getInventory());
            }
            return;
        } else if (breakBlockHoe && MaterialTags.HOES.isTagged(item)) {
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

    private Material getInteraction(MaterialSetTag tool, Material block) {
        // I wish I could use a switch statement here, but IntelliJ kept screaming at me :(
        if (tool.equals(AXES)) {
            switch (block) {
                case ACACIA_LOG -> {return Material.STRIPPED_ACACIA_LOG;}
                case ACACIA_WOOD -> {return Material.STRIPPED_ACACIA_WOOD;}
                case BAMBOO_BLOCK -> {return Material.STRIPPED_BAMBOO_BLOCK;}
                case BIRCH_LOG -> {return Material.STRIPPED_BIRCH_LOG;}
                case BIRCH_WOOD -> {return Material.STRIPPED_BIRCH_WOOD;}
                case CHERRY_LOG -> {return Material.STRIPPED_CHERRY_LOG;}
                case CHERRY_WOOD -> {return Material.STRIPPED_CHERRY_WOOD;}
                case CRIMSON_HYPHAE -> {return Material.STRIPPED_CRIMSON_HYPHAE;}
                case CRIMSON_STEM -> {return Material.STRIPPED_CRIMSON_STEM;}
                case DARK_OAK_LOG -> {return Material.STRIPPED_DARK_OAK_LOG;}
                case DARK_OAK_WOOD -> {return Material.STRIPPED_DARK_OAK_WOOD;}
                case JUNGLE_LOG -> {return Material.STRIPPED_JUNGLE_LOG;}
                case JUNGLE_WOOD -> {return Material.STRIPPED_JUNGLE_WOOD;}
                case MANGROVE_LOG -> {return Material.STRIPPED_MANGROVE_LOG;}
                case MANGROVE_WOOD -> {return Material.STRIPPED_MANGROVE_WOOD;}
                case OAK_LOG -> {return Material.STRIPPED_OAK_LOG;}
                case OAK_WOOD -> {return Material.STRIPPED_OAK_WOOD;}
                case SPRUCE_LOG -> {return Material.STRIPPED_SPRUCE_LOG;}
                case SPRUCE_WOOD -> {return Material.STRIPPED_SPRUCE_WOOD;}
                case WARPED_HYPHAE -> {return Material.STRIPPED_WARPED_HYPHAE;}
                case WARPED_STEM -> {return Material.STRIPPED_WARPED_STEM;}
                // This is when I started to go insane teeheeeee
                case WAXED_CHISELED_COPPER, EXPOSED_CHISELED_COPPER -> {return Material.CHISELED_COPPER;}
                case WAXED_COPPER_BLOCK, EXPOSED_COPPER -> {return Material.COPPER_BLOCK;}
                case WAXED_COPPER_BULB, EXPOSED_COPPER_BULB -> {return Material.COPPER_BULB;}
                case WAXED_COPPER_DOOR, EXPOSED_COPPER_DOOR -> {return Material.COPPER_DOOR;}
                case WAXED_COPPER_GRATE, EXPOSED_COPPER_GRATE -> {return Material.COPPER_GRATE;}
                case WAXED_COPPER_TRAPDOOR, EXPOSED_COPPER_TRAPDOOR -> {return Material.COPPER_TRAPDOOR;}
                case WAXED_CUT_COPPER, EXPOSED_CUT_COPPER -> {return Material.CUT_COPPER;}
                case WAXED_CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_SLAB -> {return Material.CUT_COPPER_SLAB;}
                case WAXED_CUT_COPPER_STAIRS, EXPOSED_CUT_COPPER_STAIRS -> {return Material.CUT_COPPER_STAIRS;}
                case WAXED_EXPOSED_CHISELED_COPPER, WEATHERED_CHISELED_COPPER -> {return Material.EXPOSED_CHISELED_COPPER;}
                case WAXED_EXPOSED_COPPER, WEATHERED_COPPER -> {return Material.EXPOSED_COPPER;}
                case WAXED_EXPOSED_COPPER_BULB, WEATHERED_COPPER_BULB -> {return Material.EXPOSED_COPPER_BULB;}
                case WAXED_EXPOSED_COPPER_DOOR, WEATHERED_COPPER_DOOR -> {return Material.EXPOSED_COPPER_DOOR;}
                case WAXED_EXPOSED_COPPER_GRATE, WEATHERED_COPPER_GRATE -> {return Material.EXPOSED_COPPER_GRATE;}
                case WAXED_EXPOSED_COPPER_TRAPDOOR, WEATHERED_COPPER_TRAPDOOR -> {return Material.EXPOSED_COPPER_TRAPDOOR;}
                case WAXED_EXPOSED_CUT_COPPER, WEATHERED_CUT_COPPER -> {return Material.EXPOSED_CUT_COPPER;}
                case WAXED_EXPOSED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB -> {return Material.EXPOSED_CUT_COPPER_SLAB;}
                case WAXED_EXPOSED_CUT_COPPER_STAIRS, WEATHERED_CUT_COPPER_STAIRS -> {return Material.EXPOSED_CUT_COPPER_STAIRS;}
                case WAXED_OXIDIZED_CHISELED_COPPER -> {return Material.OXIDIZED_CHISELED_COPPER;}
                case WAXED_OXIDIZED_COPPER -> {return Material.OXIDIZED_COPPER;}
                case WAXED_OXIDIZED_COPPER_BULB -> {return Material.OXIDIZED_COPPER_BULB;}
                case WAXED_OXIDIZED_COPPER_DOOR -> {return Material.OXIDIZED_COPPER_DOOR;}
                case WAXED_OXIDIZED_COPPER_GRATE -> {return Material.OXIDIZED_COPPER_GRATE;}
                case WAXED_OXIDIZED_COPPER_TRAPDOOR -> {return Material.OXIDIZED_COPPER_TRAPDOOR;}
                case WAXED_OXIDIZED_CUT_COPPER -> {return Material.OXIDIZED_CUT_COPPER;}
                case WAXED_OXIDIZED_CUT_COPPER_SLAB -> {return Material.OXIDIZED_CUT_COPPER_SLAB;}
                case WAXED_OXIDIZED_CUT_COPPER_STAIRS -> {return Material.OXIDIZED_CUT_COPPER_STAIRS;}
                case WAXED_WEATHERED_CHISELED_COPPER, OXIDIZED_CHISELED_COPPER -> {return Material.WEATHERED_CHISELED_COPPER;}
                case WAXED_WEATHERED_COPPER, OXIDIZED_COPPER -> {return Material.WEATHERED_COPPER;}
                case WAXED_WEATHERED_COPPER_BULB, OXIDIZED_COPPER_BULB -> {return Material.WEATHERED_COPPER_BULB;}
                case WAXED_WEATHERED_COPPER_DOOR, OXIDIZED_COPPER_DOOR -> {return Material.WEATHERED_COPPER_DOOR;}
                case WAXED_WEATHERED_COPPER_GRATE, OXIDIZED_COPPER_GRATE -> {return Material.WEATHERED_COPPER_GRATE;}
                case WAXED_WEATHERED_COPPER_TRAPDOOR, OXIDIZED_COPPER_TRAPDOOR -> {return Material.WEATHERED_COPPER_TRAPDOOR;}
                case WAXED_WEATHERED_CUT_COPPER, OXIDIZED_CUT_COPPER -> {return Material.WEATHERED_CUT_COPPER;}
                case WAXED_WEATHERED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_SLAB -> {return Material.WEATHERED_CUT_COPPER_SLAB;}
                case WAXED_WEATHERED_CUT_COPPER_STAIRS, OXIDIZED_CUT_COPPER_STAIRS -> {return Material.WEATHERED_CUT_COPPER_STAIRS;}
                default -> {return null;}
            }
        } else if (tool.equals(MaterialTags.SHOVELS)) {
            switch (block) {
                case GRASS_BLOCK, DIRT, COARSE_DIRT, ROOTED_DIRT, PODZOL, MYCELIUM -> {return Material.DIRT_PATH;}
                case CAMPFIRE -> {return Material.CAMPFIRE;}
                case SOUL_CAMPFIRE -> {return Material.SOUL_CAMPFIRE;}
                default -> {return null;}
            }
        } else if (tool.equals(MaterialTags.HOES)) {
            switch (block) {
                case GRASS_BLOCK, DIRT, DIRT_PATH -> {return Material.FARMLAND;}
                case COARSE_DIRT, ROOTED_DIRT -> {return Material.DIRT;}
                default -> {return null;}
            }
        } else {
            return null;
        }
    }

    private boolean isAxeInteraction(Material item) {
        return (getInteraction(AXES, item) != null);
    }

    private boolean isShovelInteraction(Material item) {
        return (getInteraction(SHOVELS, item) != null);
    }

    private boolean isHoeInteraction(Material item) {
        return (getInteraction(HOES, item) != null);
    }
}
