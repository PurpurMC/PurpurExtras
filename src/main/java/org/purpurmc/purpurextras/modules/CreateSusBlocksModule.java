package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtras;
import java.util.List;

/**
 * If enabled, players will be able to shift-right click on sand and gravel with items in their hands to create
 * suspicious blocks and put held item inside. Held item will disappear from player's hand and will be added as loot
 * inside the suspicious block. Only one item can be added per block.
 */
public class CreateSusBlocksModule implements PurpurExtrasModule, Listener {

    protected CreateSusBlocksModule() {}

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        Boolean susBlocksEnabled = PurpurExtras.getPurpurConfig().getBoolean("settings.suspicious-blocks.enabled", PurpurExtras.getPurpurConfig().getBooleanIfExists("settings.create-suspicious-blocks", false));
        Boolean exclusionListStatus = PurpurExtras.getPurpurConfig().getBoolean("settings.suspicious-blocks.enable-item-exclusion-list", false);
        List<String> exclusionList = PurpurExtras.getPurpurConfig().getList("settings.suspicious-blocks.item-exclusion-list", List.of("SHULKER_BOX"));
        return susBlocksEnabled;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractWithSusBlock(PlayerInteractEvent event, Boolean exclusionListStatus, List<String> exclusionList) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (exclusionListStatus && exclusionList.contains(event.getItem().getType().name())) return;
        if (block.getType() != Material.SAND && block.getType() != Material.GRAVEL) return;
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        switch (block.getType()) {
            case SAND -> block.setType(Material.SUSPICIOUS_SAND);
            case GRAVEL -> block.setType(Material.SUSPICIOUS_GRAVEL);
            default -> {
                return;
            }
        }

        BlockState blockState = block.getState();
        if (!(blockState instanceof BrushableBlock brushableBlock)) return;
        brushableBlock.setItem(itemStack.asOne());
        event.getItem().setAmount(itemStack.getAmount() - 1);
        brushableBlock.update();
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;
        player.swingHand(hand);
    }
}
