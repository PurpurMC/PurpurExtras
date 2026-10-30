package org.purpurmc.purpurextras;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static PurpurExtras instance;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
        } catch (ClassNotFoundException e) {
            getLogger().warning("---------------------------------------------");
            getLogger().warning("Some features may not work without Purpur!");
            getLogger().warning("PurpurExtras was created to compliment Purpur, and it appears you're not using it!");
            getLogger().warning("Purpur is a drop-in replacement for " + getServer().getName() + ".");
            getLogger().warning("You can get Purpur on https://purpurmc.org/downloads");
            getLogger().warning("---------------------------------------------");
        }

        instance = this;
        config = new PurpurConfig();

        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands -> {
                    commands.registrar().register(PurpurExtrasCommand.createCommand());
                }
        );

        PurpurExtrasModule.reloadModules();
        config.saveConfig();
    }

    public static PurpurConfig getPurpurConfig() {
        return config;
    }

    public static PurpurExtras getInstance() {
        return instance;
    }

    void reloadPurpurExtrasConfig(CommandSender commandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            config = new PurpurConfig();
            PurpurExtrasModule.reloadModules();
            config.saveConfig();
            commandSender.sendMessage(Component.text("PurpurExtras configuration reloaded!"));
        });
    }

    public static NamespacedKey key(String string) {
        return new NamespacedKey(PurpurExtras.getInstance(), string);
    }

}
