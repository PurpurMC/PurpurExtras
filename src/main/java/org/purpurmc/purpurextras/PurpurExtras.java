package org.purpurmc.purpurextras;

import org.purpurmc.purpurextras.command.FancyCommand;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static PurpurExtras instance;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
        } catch (ClassNotFoundException e) {
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0PurpurExtras was created to compliment Purpur, and it appears you're not using it!"));
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0Purpur is a drop-in replacement for " + getServer().getName() + "."));
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0You can get Purpur on https://purpurmc.org/downloads"));
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        instance = this;
        config = new PurpurConfig();

        PluginCommand command = getCommand("purpurextras");
        if (command != null) {
            command.setExecutor(new PurpurExtrasCommand());
            getServer().getPluginManager().registerEvents(new FancyCommand(), this);
        }

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
            reloadConfig();
            config.saveConfig();
            PurpurExtrasModule.reloadModules();
            commandSender.sendMessage(Component.text("PurpurExtras configuration reloaded!"));
        });
    }

    public static NamespacedKey key(String string) {
        return new NamespacedKey(PurpurExtras.getInstance(), string);
    }

}
