package org.purpurmc.purpurextras.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import org.purpurmc.purpurextras.PurpurExtras;

public class ReloadCommand extends CommandAPICommand {
    public ReloadCommand() {
        super("reload");
        withPermission("purpurextras.reload");
        executes((sender, args) -> {
            sender.sendMessage(Component.text("Reloading PurpurExtras config..."));
            PurpurExtras.getInstance().reloadPurpurExtrasConfig(sender);
        });
    }
}
