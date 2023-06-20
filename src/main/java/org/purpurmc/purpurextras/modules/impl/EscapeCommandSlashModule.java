package org.purpurmc.purpurextras.modules.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Allows players to send a message with a slash at the start by escaping it with backslash
 * (\/command that will appear as /command in chat).
 */
@ModuleInfo(name = "Escape Chat Slashes", description = "Allows you to start messages with a /!")
public class EscapeCommandSlashModule extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.chat.escape-commands";
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
