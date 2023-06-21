package org.purpurmc.purpurextras.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.purpurmc.purpurextras.modules.IModuleManager;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class EnabledModulesCommand extends CommandAPICommand {
    public EnabledModulesCommand() {
        super("modules");
        executes((sender, args) -> {
            sender.sendMessage(text("Currently Enabled Modules: ", DARK_PURPLE));
            Bukkit.getServicesManager().getRegistration(IModuleManager.class).getProvider()
                    .getModules(PurpurExtrasModule::isEnabled).forEach(m -> {
                        sender.sendMessage(text("* ", LIGHT_PURPLE)
                                .append(text(m.anno().name(), GRAY))
                                .hoverEvent(HoverEvent.showText(text(m.anno().description()))));
                    });
        });
    }
}
