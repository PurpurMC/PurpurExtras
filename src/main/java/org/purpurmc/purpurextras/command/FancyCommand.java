package org.purpurmc.purpurextras.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FancyCommand implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommandRegister(final CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (event.getCommand().getName().equalsIgnoreCase("purpurextras")) {
            event.setLiteral(
                    LiteralArgumentBuilder.<BukkitBrigadierCommandSource>literal(event.getCommandLabel())
                            .then(LiteralArgumentBuilder.literal("reload"))
                            .then(LiteralArgumentBuilder.literal("version"))
                            .build()
            );
        }


    }

}
