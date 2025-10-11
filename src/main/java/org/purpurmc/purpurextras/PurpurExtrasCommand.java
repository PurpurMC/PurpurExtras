package org.purpurmc.purpurextras;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class PurpurExtrasCommand {

    private static final String reloadCommand = "reload";
    private static final Permission reloadPermission = new Permission("purpurextras.reload");
    private static final String versionCommand = "version";
    private static final String purpurExtrasCommand = "purpurextras";

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal(purpurExtrasCommand)
                .requires(css -> css.getSender().hasPermission("purpurextras.command"))
                .executes(PurpurExtrasCommand::executeDefault)
                .then(Commands.literal(reloadCommand)
                        .requires(css -> css.getSender().hasPermission(reloadPermission))
                        .executes(PurpurExtrasCommand::executeReload))
                .then(Commands.literal(versionCommand)
                        .executes(PurpurExtrasCommand::executeVersion))
                .build();

    }


    public static int executeDefault(CommandContext<CommandSourceStack> ctx) {
        //I am making this fancier because its cooler
        ctx.getSource().getSender().sendRichMessage("<gradient:#8d54ff:#adf3ff><b>PurpurExtras</b></gradient> <white>by the Purpur Team");
        return Command.SINGLE_SUCCESS;
    }


    public static int executeVersion(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendRichMessage("<gradient:#8d54ff:#adf3ff><b>PurpurExtras</b></gradient> version: " + PurpurExtras.getInstance().getDescription().getVersion());
        return Command.SINGLE_SUCCESS;
    }

    public static int executeReload(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        sender.sendRichMessage("Reloading PurpurExtras config...");
        PurpurExtras.getInstance().reloadPurpurExtrasConfig(sender);
        return Command.SINGLE_SUCCESS;
    }

}
