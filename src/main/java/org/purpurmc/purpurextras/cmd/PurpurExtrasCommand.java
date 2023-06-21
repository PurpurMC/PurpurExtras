package org.purpurmc.purpurextras.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PurpurExtrasCommand {

    public PurpurExtrasCommand() {
        new CommandAPICommand("purpurextras")
                .withSubcommands(new ReloadCommand(), new VersionCommand())
                .withPermission("purpurextras.command")
                .executes((CommandExecutor) (sender, args) -> sender.sendMessage(
                        Component.text("PurpurExtras", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                        .append(Component.text(" by YouHaveTrouble"))))
                .register();
    }
}
