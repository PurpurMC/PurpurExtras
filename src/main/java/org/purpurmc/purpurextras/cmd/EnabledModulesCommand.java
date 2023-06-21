package org.purpurmc.purpurextras.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.event.HoverEvent;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class EnabledModulesCommand extends CommandAPICommand {
    public EnabledModulesCommand() {
        super("modules");
        executes((sender, args) -> {
           sender.sendMessage(text("Currently Enabled Modules: ", DARK_PURPLE));
            PurpurExtras.getInstance().getModuleManager().getModules(PurpurExtrasModule::isEnabled).forEach(m -> {
                sender.sendMessage(text("* ", LIGHT_PURPLE)
                        .append(text(m.anno().name(), GRAY))
                        .hoverEvent(HoverEvent.showText(text(m.anno().description()))));
            });
        });
    }
}
