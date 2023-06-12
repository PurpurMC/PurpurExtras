package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
        if (!(event.getBlock().getState() instanceof Lootable lootable)) return;
        if (!(lootable.hasLootTable())) return;
        if (event.getPlayer().hasPermission(permission)) return;
        if (allowBreakingInSneak && event.getPlayer().isSneaking()) return;

        event.setCancelled(true);
        switch (messageType) {
            case CHAT -> event.getPlayer().sendMessage(message);
            case ACTION_BAR -> event.getPlayer().sendActionBar(message);
        }
    }

}
