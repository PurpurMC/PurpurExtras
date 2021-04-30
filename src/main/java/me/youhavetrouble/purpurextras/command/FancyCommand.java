package me.youhavetrouble.purpurextras.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FancyCommand implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommandRegister(final @NonNull CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (event.getCommand().getName().equalsIgnoreCase("purpurextras")) {
            event.setLiteral(
                    LiteralArgumentBuilder.<BukkitBrigadierCommandSource>literal(event.getCommandLabel())
                            .then(LiteralArgumentBuilder.literal("reload"))
                            .build()
            );
        }


    }

}
