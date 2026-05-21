package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.Component;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.purpurmc.purpurextras.util.MessageType;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * If enabled, players will be able to shift-right click on sand and gravel with items in their hands to create
 * suspicious blocks and put held item inside. Held item will disappear from player's hand and will be added as loot
 * inside the suspicious block. Only one item can be added per block.
 */
public class CreateSusBlocksModule implements PurpurExtrasModule, Listener {

    protected CreateSusBlocksModule() {}

    private boolean exclusionListStatus;
    private final Set<String> exclusions = new HashSet<>();
    private Component exclusionMessage;
    private MessageType messageType;

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        this.exclusionListStatus = PurpurExtras.getPurpurConfig().getBoolean("settings.suspicious-blocks.exclusion-list.enable-item-exclusion-list", false);
        List<String> exclusionList = PurpurExtras.getPurpurConfig().getList("settings.suspicious-blocks.exclusion-list.item-exclusion-list", List.of("shulker_box"));
        for (String exclusion : exclusionList) {
            exclusions.add(exclusion.toLowerCase(Locale.ENGLISH));
        }
        String rawExclusionMessage = PurpurExtras.getPurpurConfig().getString("settings.suspicious-blocks.exclusion-list.item-excluded-message", "<red>The item you're using is on the excluded list!");
        this.exclusionMessage = rawExclusionMessage.isBlank() ? null : MiniMessage.miniMessage().deserialize(rawExclusionMessage);
        try {
            this.messageType = MessageType.valueOf(PurpurExtras.getPurpurConfig().getString("settings.suspicious-blocks.exclusion-list.message-type", "CHAT").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.messageType = MessageType.CHAT;
        }
        return PurpurExtras.getPurpurConfig().getBoolean("settings.suspicious-blocks.enabled", PurpurExtras.getPurpurConfig().getBooleanAndRemove("settings.create-suspicious-blocks", false));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractWithSusBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.SAND && block.getType() != Material.GRAVEL) return;
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (this.exclusionListStatus && this.exclusions.contains(itemStack.getType().name().toLowerCase(Locale.ENGLISH))) {
            if (this.exclusionMessage == null) return;
            switch (this.messageType) {
                case CHAT -> player.sendMessage(this.exclusionMessage);
                case ACTION_BAR -> player.sendActionBar(this.exclusionMessage);
            }
            return;
        }

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
