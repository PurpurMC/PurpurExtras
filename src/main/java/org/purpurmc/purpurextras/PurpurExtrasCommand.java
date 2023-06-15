package org.purpurmc.purpurextras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PurpurExtrasCommand implements TabExecutor {
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


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> cmds = List.of("reload", "version");
        if(args.length == 1) {
            return cmds.stream().filter(s -> s.toLowerCase().startsWith(args[0])).toList();
        }else {
            return Collections.emptyList();
        }
    }
}
