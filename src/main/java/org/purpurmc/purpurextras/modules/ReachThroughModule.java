package org.purpurmc.purpurextras.modules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Attachable;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashSet;

public class ReachThroughModule implements PurpurExtrasModule, Listener {
    private boolean bypassEmptyFrames, bypassFilledFrames, bypassUnwaxedSigns, bypassWaxedSigns, bypassPaintings;

    private final HashSet<EntityType> passableEntities = new HashSet<>();

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (bypassEmptyFrames || bypassFilledFrames) {
            passableEntities.add(EntityType.ITEM_FRAME);
            passableEntities.add(EntityType.GLOW_ITEM_FRAME);
        }
        if (bypassPaintings) passableEntities.add(EntityType.PAINTING);
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
        Inventory containerInv = getContainerInventory(attachableBlock.getAttachedFace(), entityClicked.getLocation().toBlockLocation());
        if (containerInv == null) return;
        interactEntityEvent.setCancelled(true);
        player.openInventory(containerInv);

    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent interactEvent){
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
        Inventory inventoryClicked = getContainerInventory(face, blockClicked.getLocation());
        if (inventoryClicked == null) return;
        interactEvent.setCancelled(true);
        player.openInventory(inventoryClicked);
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

    private Inventory getContainerInventory(BlockFace face, Location location) {
        int attachedXOffset = face.getModX();
        int attachedYOffset = face.getModY();
        int attachedZOffset = face.getModZ();
        Location attachedBlockLocation = location.add(attachedXOffset, attachedYOffset, attachedZOffset);
        Block blockAtLocation = attachedBlockLocation.getBlock();
        if (!(blockAtLocation.getState() instanceof Container containerBlock)) return null;
        return containerBlock.getInventory();
    }

    private boolean signPassthroughEnabled(Sign sign){
        if (sign.isWaxed() && !bypassWaxedSigns) return false;
        if (!sign.isWaxed() && !bypassUnwaxedSigns) return false;
        return true;
    }

    private boolean itemFramePassthroughEnabled(ItemFrame itemFrame){
        if (itemFrame.getItem().isEmpty() && !bypassEmptyFrames) return false;
        return true;
    }
}
