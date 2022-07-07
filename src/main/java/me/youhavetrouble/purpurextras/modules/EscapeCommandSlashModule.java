package me.youhavetrouble.purpurextras.modules;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.youhavetrouble.purpurextras.PurpurExtras;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EscapeCommandSlashModule implements PurpurExtrasModule, Listener {

    protected EscapeCommandSlashModule() {}
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.chat.escape-commands", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommandEscape(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String[] messageSplit = message.split(" ");
        String command = messageSplit[0].substring(1);
        Component component = event.message().replaceText(
                TextReplacementConfig.builder()
                        .match("(\\\\/\\S*)")
                        .replacement(command)
                        .once()
                        .build());
        event.message(component);
    }
}
