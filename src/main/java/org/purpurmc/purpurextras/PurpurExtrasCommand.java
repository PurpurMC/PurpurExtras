package org.purpurmc.purpurextras;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PurpurExtrasCommand {

    private static final String reloadCommand = "reload";
    private static final Permission reloadPermission = new Permission("purpurextras.reload");
    private static final String versionCommand = "version";
    private static final String purpurExtrasCommand = "purpurextras";

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal(purpurExtrasCommand)
                .requires(css -> css.getSender().hasPermission("purpurextras.command"))
                .executes(PurpurExtrasCommand::executeDefault)
                .then(Commands.argument("command", StringArgumentType.word())
                        .suggests(PurpurExtrasCommand::suggestSubCommands)
                        .executes(PurpurExtrasCommand::execute)).build();

    }


    public static int executeDefault(CommandContext<CommandSourceStack> ctx) {
        //I am making this fancier because its cooler
        ctx.getSource().getSender().sendRichMessage("<gradient:#8d54ff:#adf3ff><b>PurpurExtras</b></gradient> <white>by YouHaveTrouble");
        return Command.SINGLE_SUCCESS;
    }

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        String argument = StringArgumentType.getString(ctx, "command");
        if (argument.equalsIgnoreCase(reloadCommand)) return executeReload(sender);
        if (argument.equalsIgnoreCase(versionCommand)) return executeVersion(sender);
        sender.sendRichMessage("<gray>Unknown Command</gray>");
        return Command.SINGLE_SUCCESS;
    }

    public static int executeVersion(CommandSender sender) {
        sender.sendRichMessage("<gradient:#8d54ff:#adf3ff><b>PurpurExtras</b></gradient> version: " + PurpurExtras.getInstance().getDescription().getVersion());
        return Command.SINGLE_SUCCESS;
    }

    public static int executeReload(CommandSender sender) throws CommandSyntaxException {
        if (!sender.hasPermission(reloadPermission)) {
            throw NO_PERMISSION.create();
        }
        sender.sendRichMessage("Reloading PurpurExtras config...");
        PurpurExtras.getInstance().reloadPurpurExtrasConfig(sender);
        return Command.SINGLE_SUCCESS;
    }


    private static CompletableFuture<Suggestions> suggestSubCommands(@NotNull CommandContext<?> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        CommandSender sender = css.getSender();

        if (sender.hasPermission(reloadPermission) && reloadCommand.startsWith(builder.getRemainingLowerCase()))
            builder.suggest(reloadCommand);
        if (versionCommand.startsWith(builder.getRemainingLowerCase())) builder.suggest(versionCommand);
        return builder.buildFuture();
    }

    private static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    PurpurExtras.getInstance().miniMessage.deserialize(
                            "<red>You don't have permission to do that.</red>"
                    )
            )
    );


}
