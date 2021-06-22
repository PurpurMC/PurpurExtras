package me.youhavetrouble.purpurextras.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EscapeCommandSlashListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommandEscape(AsyncChatEvent event) {
        String message = PlainComponentSerializer.plain().serialize(event.message());
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
