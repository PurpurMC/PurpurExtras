package org.purpurmc.purpurextras;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.purpurmc.purpurextras.modules.JoinFullServerModule;
import org.purpurmc.purpurextras.modules.JoinFullServerModule.JFSUser;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JFSListCommand {

    private static final String PERMISSION = "purpurextras.jfslist";
    private static final int FAIL = 0;

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("jfslist")
                .requires(css -> css.getSender().hasPermission(PERMISSION) && JoinFullServerModule.getInstance() != null)
                .executes(JFSListCommand::executeList)
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", ArgumentTypes.playerProfiles())
                                .suggests(JFSListCommand::suggestNotInList)
                                .executes(JFSListCommand::executeAdd)))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", ArgumentTypes.playerProfiles())
                                .suggests(JFSListCommand::suggestInList)
                                .executes(JFSListCommand::executeRemove)))
                .then(Commands.literal("list")
                        .executes(JFSListCommand::executeList))
                .then(Commands.literal("reload")
                        .executes(JFSListCommand::executeReload))
                .build();
    }

    private static JoinFullServerModule getModule(CommandSender sender) {
        JoinFullServerModule module = JoinFullServerModule.getInstance();
        if (module == null) {
            sender.sendRichMessage("<red>The Join Full Server module is disabled.");
        }
        return module;
    }

    private static CompletableFuture<Suggestions> suggestInList(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        JoinFullServerModule module = JoinFullServerModule.getInstance();
        if (module == null) return builder.buildFuture();

        List<JFSUser> users = module.getUsers();
        if (users.isEmpty()) return builder.buildFuture();

        String remaining = builder.getRemaining().toLowerCase();

        for (JFSUser user : users) {
            String name = user.name();
            if (name != null && name.toLowerCase().startsWith(remaining)) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestNotInList(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        JoinFullServerModule module = JoinFullServerModule.getInstance();
        if (module == null) return builder.buildFuture();

        List<JFSUser> users = module.getUsers();
        Set<String> listUuids = Set.copyOf(users.stream().map(JFSUser::uuid).toList());
        String remaining = builder.getRemaining().toLowerCase();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            String name = player.getName();
            if (name != null && !listUuids.contains(player.getUniqueId().toString())) {
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
        }

        return builder.buildFuture();
    }

    private static int executeAdd(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        JoinFullServerModule module = getModule(sender);
        if (module == null) return FAIL;

        try {
            PlayerProfileListResolver profileResolver = ctx.getArgument("targets", PlayerProfileListResolver.class);
            Collection<PlayerProfile> profiles = profileResolver.resolve(ctx.getSource());

            if (profiles.isEmpty()) {
                sender.sendRichMessage("<red>Player not found.");
                return FAIL;
            }

            for (PlayerProfile profile : profiles) {
                UUID uuid = profile.getId();
                if (uuid == null) continue;

                String name = profile.getName() != null ? profile.getName() : uuid.toString();

                if (module.addUser(uuid, profile.getName())) {
                    sender.sendRichMessage("<white>Added " + name + " to the Join Full Server list.");
                } else {
                    sender.sendRichMessage("<white>" + name + " is already on the Join Full Server list.");
                }
            }
        } catch (Exception e) {
            sender.sendRichMessage("<red>Player not found.");
            return FAIL;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        JoinFullServerModule module = getModule(sender);
        if (module == null) return FAIL;

        try {
            PlayerProfileListResolver profileResolver = ctx.getArgument("targets", PlayerProfileListResolver.class);
            Collection<PlayerProfile> profiles = profileResolver.resolve(ctx.getSource());

            if (profiles.isEmpty()) {
                sender.sendRichMessage("<red>Player not found.");
                return FAIL;
            }

            for (PlayerProfile profile : profiles) {
                UUID uuid = profile.getId();
                if (uuid == null) continue;

                String name = profile.getName() != null ? profile.getName() : uuid.toString();

                if (module.removeUser(uuid)) {
                    sender.sendRichMessage("<white>Removed " + name + " from the Join Full Server list.");
                } else {
                    sender.sendRichMessage("<white>" + name + " is not on the Join Full Server list.");
                }
            }
        } catch (Exception e) {
            sender.sendRichMessage("<red>Player not found.");
            return FAIL;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        JoinFullServerModule module = getModule(sender);
        if (module == null) return FAIL;

        List<JFSUser> users = module.getUsers();
        if (users.isEmpty()) {
            sender.sendRichMessage("<white>The Join Full Server list is empty.");
            return Command.SINGLE_SUCCESS;
        }

        sender.sendRichMessage("<white>Join Full Server List (" + users.size() + "):");

        for (JFSUser user : users) {
            sender.sendRichMessage("<white>- " + user.name() + " (" + user.uuid() + ")");
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeReload(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        JoinFullServerModule module = getModule(sender);
        if (module == null) return FAIL;

        module.reload();
        sender.sendRichMessage("<white>The Join Full Server list has been reloaded.");
        return Command.SINGLE_SUCCESS;
    }
}
