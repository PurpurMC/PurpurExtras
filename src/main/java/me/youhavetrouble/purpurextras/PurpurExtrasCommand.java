package me.youhavetrouble.purpurextras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PurpurExtrasCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 0) {
            commandSender.sendMessage(
                    Component.text("PurpurExtras", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                    .append(Component.text(" by YouHaveTrouble"))
            );
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("purpurextras.reload")) {
                commandSender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
                return true;
            }
            commandSender.sendMessage(Component.text("Reloading PurpurExtras config..."));
            PurpurExtras.getInstance().reloadPurpurExtrasConfig(commandSender);
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("version")) {
            commandSender.sendMessage(Component.text("PurpurExtras version "+PurpurExtras.getInstance().getDescription().getVersion()));
        }

        return true;
    }
}
