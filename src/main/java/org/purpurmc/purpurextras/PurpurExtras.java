package org.purpurmc.purpurextras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.purpurmc.purpurextras.modules.ModuleManager;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static ModuleManager moduleManager;
    private static PurpurExtras instance;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
        } catch (ClassNotFoundException e) {
            getLogger().warning("PurpurExtras was created to compliment Purpur, and it appears you're not using it!");
            getLogger().warning("Purpur is a drop-in replacement for " + getServer().getName() + ".");
            getLogger().warning("You can get Purpur on https://purpurmc.org/downloads");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        config = new PurpurConfig();
        moduleManager = new ModuleManager();

        PluginCommand command = getCommand("purpurextras");
        if (command != null) {
            PurpurExtrasCommand cmd = new PurpurExtrasCommand();
            command.setExecutor(cmd);
            command.setTabCompleter(cmd);
        }

        moduleManager.reloadModules();
        config.saveConfig();
    }

    public static PurpurConfig getPurpurConfig() {
        return config;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static PurpurExtras getInstance() {
        return instance;
    }

    void reloadPurpurExtrasConfig(CommandSender commandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            config = new PurpurConfig();
            moduleManager.reloadModules();
            config.saveConfig();
            commandSender.sendMessage(Component.text("PurpurExtras configuration reloaded!"));
        });
    }

    public static NamespacedKey key(String string) {
        return new NamespacedKey(PurpurExtras.getInstance(), string);
    }

}
