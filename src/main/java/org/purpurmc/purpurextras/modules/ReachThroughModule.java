package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Crafter;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.Smoker;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.material.Attachable;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashSet;
import java.util.List;

public class ReachThroughModule implements PurpurExtrasModule, Listener {
    private boolean reachThroughEmptyFrames, reachThroughFilledFrames, reachThroughSigns, reachThroughPaintings,
            openBlockedContainers, triggerPiglinAggro, incrementStatistics;

    private final HashSet<EntityType> passableEntities = new HashSet<>();

    public ReachThroughModule() {
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (reachThroughEmptyFrames || reachThroughFilledFrames) {
            passableEntities.add(EntityType.ITEM_FRAME);
            passableEntities.add(EntityType.GLOW_ITEM_FRAME);
        }
        if (reachThroughPaintings) passableEntities.add(EntityType.PAINTING);
        openBlockedContainers = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.open-blocked-containers", false);
        triggerPiglinAggro = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.trigger-piglin-aggro", true);
        incrementStatistics = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.increment-statistics", true);
    }

    @Override
    public boolean shouldEnable() {
        reachThroughEmptyFrames = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.item-frames.empty", false);
        reachThroughFilledFrames = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.item-frames.filled", false);
        reachThroughPaintings = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.paintings", false);
        reachThroughSigns = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.signs.waxed", false);
        return reachThroughEmptyFrames || reachThroughFilledFrames || reachThroughPaintings || reachThroughSigns;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent interactEntityEvent) {
        if (interactEntityEvent.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player player = interactEntityEvent.getPlayer();
        if (player.isSneaking()) return;

        Entity entityClicked = interactEntityEvent.getRightClicked();
        if (!passableEntities.contains(entityClicked.getType())) return;
        if (!(entityClicked instanceof Attachable attachableBlock)) return;
        if ((entityClicked instanceof ItemFrame itemFrame) && !canReachThroughItemFrame(itemFrame)) return;

        BlockFace face = attachableBlock.getAttachedFace();
        if (!openedInventory(player, getBlockFromBlockFace(face,
                interactEntityEvent.getRightClicked().getLocation()), face)) return;
        interactEntityEvent.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent interactEvent) {
        if (!reachThroughSigns) return;
        if (interactEvent.getHand() == null || interactEvent.getHand().equals(EquipmentSlot.OFF_HAND) || interactEvent.getAction().isLeftClick())
            return;
        Player player = interactEvent.getPlayer();
        Block blockClicked = interactEvent.getClickedBlock();
        if (player.isSneaking()) return;
        if (blockClicked == null) return;
        if (!(blockClicked.getState() instanceof Sign signClicked) || !(blockClicked.getBlockData() instanceof Directional directionalBlock))
            return;
        if (!signClicked.isWaxed()) return;
        BlockFace face = directionalBlock.getFacing().getOppositeFace();
        if (!openedInventory(player, getBlockFromBlockFace(face,
                blockClicked.getLocation()), face)) return;
        interactEvent.setCancelled(true);
    }

    @SuppressWarnings({"UnstableApiUsage", "BooleanMethodIsAlwaysInverted"})
    private boolean openedInventory(Player player, Block block, BlockFace face) {
        PlayerInteractEvent newEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK,
                player.getInventory().getItemInMainHand(), block, face, EquipmentSlot.HAND);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);
        if (newEvent.useInteractedBlock().equals(Event.Result.DENY)) return false;
        if (block.getType().equals(Material.ENDER_CHEST)) {
            openEnderchest(player);
            return true;
        }

        BlockState state = block.getState(true);
        if (!(state instanceof Container container)) return false;
        if (containerIsBlocked(container)) return false;

        InventoryView openedInventory = player.openInventory(container.getInventory());
        handleContainerBehavior(openedInventory, newEvent, player, container);
        return true;
    }


    private boolean containerIsBlocked(Container container) {
        if (openBlockedContainers) return true;
        if (container instanceof ShulkerBox shulkerBox) {
            Directional directional = (Directional) shulkerBox.getBlockData();
            BlockFace blockFace = directional.getFacing();
            Location facingBlock = shulkerBox.getLocation().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()).toBlockLocation();
            return !facingBlock.getBlock().getType().isAir();
        }
        if (container instanceof Chest chest) {
            return chest.isBlocked();
        }
        return false;
    }

    private Block getBlockFromBlockFace(BlockFace face, Location location) {
        int attachedXOffset = face.getModX();
        int attachedYOffset = face.getModY();
        int attachedZOffset = face.getModZ();
        Location attachedBlockLocation = location.add(attachedXOffset, attachedYOffset, attachedZOffset);
        return attachedBlockLocation.getBlock();
    }


    private void handleContainerBehavior(InventoryView inventoryView, PlayerInteractEvent newInteractEvent,
                                         Player player, Container container) {
        Block block = container.getBlock();
        if (!player.getOpenInventory().equals(inventoryView)) {
            newInteractEvent.setCancelled(true);
            return;
        }
        if (block.getType().equals(Material.TRAPPED_CHEST)) {
            aggroPiglins(player);
            increment(player, Statistic.TRAPPED_CHEST_TRIGGERED);
            return;
        }
        switch (container) {
            case Barrel ignored -> {
                aggroPiglins(player);
                increment(player, Statistic.OPEN_BARREL);
            }
            case Chest ignored -> {
                aggroPiglins(player);
                increment(player, Statistic.CHEST_OPENED);
            }
            case ShulkerBox ignored -> {
                aggroPiglins(player);
                increment(player, Statistic.SHULKER_BOX_OPENED);
            }
            case BlastFurnace ignored -> increment(player, Statistic.INTERACT_WITH_BLAST_FURNACE);
            case BrewingStand ignored -> increment(player, Statistic.BREWINGSTAND_INTERACTION);
            case Crafter ignored -> {
                //There is no statistic for this??
            }
            case Dispenser ignored -> increment(player, Statistic.DISPENSER_INSPECTED);
            case Dropper ignored -> increment(player, Statistic.DROPPER_INSPECTED);
            case Smoker ignored -> increment(player, Statistic.INTERACT_WITH_SMOKER);
            case Furnace ignored -> increment(player, Statistic.FURNACE_INTERACTION);
            case Hopper ignored -> increment(player, Statistic.HOPPER_INSPECTED);
            default -> {
            }
        }
    }

    private void aggroPiglins(Player player) {
        if (!triggerPiglinAggro) return;
        List<Entity> nearbyEntities = player.getNearbyEntities(15, 3, 15);
        Location playerLocation = player.getLocation().toBlockLocation();
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Piglin piglin)) continue;
            if (!piglin.hasLineOfSight(player)) continue;
            if (piglin.getLocation().distance(playerLocation) > 15) continue;
            piglin.setMemory(MemoryKey.ANGRY_AT, player.getUniqueId());
        }
    }

    private boolean canReachThroughItemFrame(ItemFrame itemFrame) {
        if (itemFrame.getItem().isEmpty()) {
            return reachThroughEmptyFrames;
        } else {
            return reachThroughFilledFrames;
        }
    }

    private void increment(Player player, Statistic statistic) {
        if (incrementStatistics) player.incrementStatistic(statistic);
    }

    private void openEnderchest(Player player) {
        InventoryView view = player.openInventory(player.getEnderChest());
        if (!player.getOpenInventory().equals(view)) return;
        increment(player, Statistic.ENDERCHEST_OPENED);
    }

}
