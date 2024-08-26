package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.BlockState;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.Vault;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.loot.Lootable;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.util.MessageType;

/**
 * Prevents players from breaking blocks with loot tables that can regenerate loot.
 */
public class LootBlocksProtectionModule implements PurpurExtrasModule, Listener {

    private final Component message;
    private MessageType messageType;
    private final boolean allowBreakingInSneak;
    private final boolean blocksImmuneToExplosions;

    private final String permission = "purpurextras.lootblockprotectionbypass";

    protected LootBlocksProtectionModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();

        DefaultPermissions.registerPermission(
                permission,
                "Players with this permission will be able to break blocks with loot tables that can regenerate loot",
                PermissionDefault.OP
        );

        String defaultMessage = "<red>Prevented you from breaking this block because it can regenerate loot. Sneak to break it anyway.";
        message = MiniMessage.miniMessage().deserialize(
                config.getString("settings.protect-blocks-with-loot.message", defaultMessage)
        );
        allowBreakingInSneak = config.getBoolean("settings.protect-blocks-with-loot.allow-breaking-in-sneak", true);
        blocksImmuneToExplosions = config.getBoolean("settings.protect-blocks-with-loot.immune-to-explosions", false);
        try {
            messageType = MessageType.valueOf(
                    config.getString("settings.protect-blocks-with-loot.message-type", "CHAT").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            messageType = MessageType.CHAT;
        }
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.protect-blocks-with-loot.enabled", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDestroyBlockWithLoot(BlockBreakEvent event) {
        // decorated pots are only lootable by breaking them
        if (isProtected(event.getBlock().getState(true))) {
            handleLootBlockDestruction(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplodingBlockWithLoot(EntityExplodeEvent event) {
        if (!blocksImmuneToExplosions) return;
        event.blockList().removeIf(block -> isProtected(block.getState(true)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplodingBlockWithLoot(BlockExplodeEvent event) {
        if (!blocksImmuneToExplosions) return;
        event.blockList().removeIf(block -> isProtected(block.getState(true)));
    }

    private void handleLootBlockDestruction(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission(permission)) return;
        if (allowBreakingInSneak && event.getPlayer().isSneaking()) return;
        event.setCancelled(true);
        switch (messageType) {
            case CHAT -> event.getPlayer().sendMessage(message);
            case ACTION_BAR -> event.getPlayer().sendActionBar(message);
        }
    }

    private boolean isProtected(BlockState blockState) {
        if (blockState instanceof DecoratedPot) return false;
        if (blockState instanceof Lootable lootable) {
            return lootable.hasLootTable();
        }
        if (blockState instanceof Vault) return true;
        return false;
    }

}
