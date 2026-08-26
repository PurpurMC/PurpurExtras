package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

public class BooksModule implements PurpurExtrasModule, Listener {

    private final String noPermissionMessageContent;

    protected BooksModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        this.noPermissionMessageContent = config.getString("settings.books.no-permission-message", "<red>You do not have permission to edit/sign books.");
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.books.enabled", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBookEdit(PlayerEditBookEvent event) {
        if (!event.getPlayer().hasPermission("purpurextras.books.modify")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(noPermissionMessageContent));
        }
    }
}
