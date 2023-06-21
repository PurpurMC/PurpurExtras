package org.purpurmc.purpurextras.command;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import org.purpurmc.purpurextras.PurpurExtras;

public class VersionCommand extends CommandAPICommand {
    public VersionCommand() {
        super("version");
        executes((sender, args) -> {
            sender.sendMessage(Component.text("PurpurExtras version "+ PurpurExtras.getInstance().getDescription().getVersion()));
        });
    }
}
