package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Attachable;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashSet;
import java.util.List;

public class ReachThroughModule implements PurpurExtrasModule, Listener {
    private boolean bypassEmptyFrames, bypassFilledFrames, bypassUnwaxedSigns, bypassWaxedSigns, bypassPaintings,
            openBlockedContainers, triggerPiglinAggro, incrementStatistics;

    private final HashSet<EntityType> passableEntities = new HashSet<>();

    public ReachThroughModule() {
        }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (bypassEmptyFrames || bypassFilledFrames) {
            passableEntities.add(EntityType.ITEM_FRAME);
            passableEntities.add(EntityType.GLOW_ITEM_FRAME);
        }
        if (bypassPaintings) passableEntities.add(EntityType.PAINTING);
        openBlockedContainers = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.open-blocked-containers", false);
        triggerPiglinAggro = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.trigger-piglin-aggro", true);
        incrementStatistics = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.options.increment-statistics", true);

    }

    @Override
    public boolean shouldEnable() {
        bypassEmptyFrames = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.item-frames.empty", false);
        bypassFilledFrames = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.item-frames.filled", false);
        bypassPaintings = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.paintings", false);
        bypassUnwaxedSigns = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.signs.unwaxed", false);
        bypassWaxedSigns = PurpurExtras.getPurpurConfig().getBoolean("settings.reach-through.signs.waxed", false);
        return bypassEmptyFrames || bypassFilledFrames || bypassPaintings || bypassUnwaxedSigns || bypassWaxedSigns;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent interactEntityEvent) {
        if (interactEntityEvent.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player player = interactEntityEvent.getPlayer();
        if (player.isSneaking()) return;
        Entity entityClicked = interactEntityEvent.getRightClicked();
        if (!passableEntities.contains(entityClicked.getType())) return;
        if (!(entityClicked instanceof Attachable attachableBlock)) return;
        if ((entityClicked instanceof ItemFrame itemFrame) && !itemFramePassthroughEnabled(itemFrame)) return;
        BlockFace face = attachableBlock.getAttachedFace();
        Container container = getContainer(face, entityClicked.getLocation().toBlockLocation());
        if (container == null) return;
        if (!canInteractWithContainer(player, container, face)) return;
        if (!canOpenContainer(container)) return;
        interactEntityEvent.setCancelled(true);
        player.openInventory(container.getInventory());
        handleContainerTypes(player, container);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent interactEvent) {
        if (!bypassWaxedSigns && !bypassUnwaxedSigns) return;
        Player player = interactEvent.getPlayer();
        Block blockClicked = interactEvent.getClickedBlock();
        if (interactEvent.getHand() == null || interactEvent.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (player.isSneaking()) return;
        if (blockClicked == null) return;
        if (!(blockClicked.getState() instanceof Sign signClicked)) return;
        if (!signPassthroughEnabled(signClicked)) return;
        if (!(blockClicked.getBlockData() instanceof Directional directionalBlock)) return;
        BlockFace face = directionalBlock.getFacing().getOppositeFace();
        Container container = getContainer(face, blockClicked.getLocation());
        if (container == null) return;
        if (!canInteractWithContainer(player, container, face)) return;
        if (!canOpenContainer(container)) return;
        interactEvent.setCancelled(true);
        player.openInventory(container.getInventory());
        handleContainerTypes(player, container);
    }

    @SuppressWarnings("UnstableApiUsage")
    private boolean canInteractWithContainer(Player player, Container containerBlock, BlockFace attachedFace) {
        Block block = containerBlock.getBlock();
        PlayerInteractEvent newEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), block, attachedFace, EquipmentSlot.HAND);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);
        return !newEvent.useInteractedBlock().equals(Event.Result.DENY);
    }

    private boolean canOpenContainer(Container container) {
        if (openBlockedContainers) return true;
        if (container instanceof ShulkerBox shulkerBox) {
            Directional directional = (Directional) shulkerBox.getBlockData();
            BlockFace blockFace = directional.getFacing();
            Location facingBlock = shulkerBox.getLocation().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()).toBlockLocation();
            return facingBlock.getBlock().getType().isAir();
        }
        if (container instanceof Chest) {
            Location location = container.getLocation().add(0, 1, 0);
            return !location.getBlock().isSuffocating();
        }
        return true;
    }

    private Container getContainer(BlockFace face, Location location) {
        int attachedXOffset = face.getModX();
        int attachedYOffset = face.getModY();
        int attachedZOffset = face.getModZ();
        Location attachedBlockLocation = location.add(attachedXOffset, attachedYOffset, attachedZOffset);
        Block blockAtLocation = attachedBlockLocation.getBlock();
        if (!(blockAtLocation.getState() instanceof Container containerBlock)) return null;
        return containerBlock;
    }

    private void aggroPiglins(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(15, 3, 15);
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Piglin piglin)) continue;
            if (!piglin.hasLineOfSight(player)) continue;
            piglin.setMemory(MemoryKey.ANGRY_AT, player.getUniqueId());
        }
    }


    private void handleContainerTypes(Player player, Container container) {
        Block block = container.getBlock();
        if (block.getType().equals(Material.TRAPPED_CHEST)) {
            if (triggerPiglinAggro) aggroPiglins(player);
            if (incrementStatistics) player.incrementStatistic(Statistic.TRAPPED_CHEST_TRIGGERED);
        }
        BlockData blockData = container.getBlock().getBlockData();
        if (blockData instanceof EnderChest) if (incrementStatistics) player.incrementStatistic(Statistic.ENDERCHEST_OPENED);

        switch (container) {
            case Barrel ignored -> {
                if (triggerPiglinAggro) aggroPiglins(player);
                if (incrementStatistics) player.incrementStatistic(Statistic.OPEN_BARREL);
            }
            case BlastFurnace ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.INTERACT_WITH_BLAST_FURNACE);
            }
            case BrewingStand ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.BREWINGSTAND_INTERACTION);
            }
            case Chest ignored -> {
                if (triggerPiglinAggro) aggroPiglins(player);
                if (incrementStatistics) player.incrementStatistic(Statistic.CHEST_OPENED);
            }
            case Crafter ignored -> {
                //There is no statistic for this??
            }
            case Dispenser ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.DISPENSER_INSPECTED);
            }
            case Dropper ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.DROPPER_INSPECTED);
            }
            case Smoker ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.INTERACT_WITH_SMOKER);
            }
            case Furnace ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.FURNACE_INTERACTION);
            }
            case Hopper ignored -> {
                if (incrementStatistics) player.incrementStatistic(Statistic.HOPPER_INSPECTED);
            }
            case ShulkerBox ignored -> {
                if (triggerPiglinAggro) aggroPiglins(player);
                if (incrementStatistics) player.incrementStatistic(Statistic.SHULKER_BOX_OPENED);
            }
            default -> {}
        }
    }

    private boolean signPassthroughEnabled(Sign sign) {
        if (sign.isWaxed() && !bypassWaxedSigns) return false;
        if (!sign.isWaxed() && !bypassUnwaxedSigns) return false;
        return true;
    }

    private boolean itemFramePassthroughEnabled(ItemFrame itemFrame) {
        if (itemFrame.getItem().isEmpty() && !bypassEmptyFrames) return false;
        return true;
    }
}
