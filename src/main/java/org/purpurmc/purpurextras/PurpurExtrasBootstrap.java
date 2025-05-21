package org.purpurmc.purpurextras;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.Command;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class PurpurExtrasBootstrap implements PluginBootstrap {

  @Override
  public void bootstrap(BootstrapContext context) {
    LiteralCommandNode<CommandSourceStack> purpurCommand = Commands.literal("purpurextras")
      .requires(source -> source.getSender().getPermission(("purpurextras.command")))
      .executes(ctx -> {
            final CommandSender commandSender = ctx.getSource().getSender();
              commandSender.sendMessage(
                  Component.text("PurpurExtras", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                  .append(Component.text(" by YouHaveTrouble"))
                );
              return Command.SINGLE_SUCCESS;
        })
      .then(Commands.literal("reload")
        .requires(source -> source.getSender().hasPermission("purpurextras.reload"))
        .executes(ctx -> {
            final CommandSender commandSender = ctx.getSource().getSender();
            commandSender.sendMessage(Component.text("Reloading PurpurExtras config..."));
            PurpurExtras.getInstance().reloadPurpurExtrasConfig(commandSender);
            return Command.SINGLE_SUCCESS;
        })
      )
      .then(Commands.literal("version")
        .executes(ctx -> {
          final CommandSender commandSender = ctx.getSource().getSender();
          commandSender.sendMessage(Component.text("PurpurExtras version "+ PurpurExtras.getInstance().getDescription().getVersion()));
          return Command.SINGLE_SUCCESS;
        })
      )
      .build();

    context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
      commands.registrar().register(purpurCommand);
    });
  }

  @Override
  public JavaPlugin createPlugin(PluginProviderContext context) {
    return new PurpurExtras();
  }
}