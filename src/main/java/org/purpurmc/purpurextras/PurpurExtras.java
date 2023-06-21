package org.purpurmc.purpurextras;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.purpurmc.purpurextras.cmd.PurpurExtrasCommand;
import org.purpurmc.purpurextras.modules.IModuleManager;
import org.purpurmc.purpurextras.modules.ModuleManager;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;

    private static PurpurExtras instance;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

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
        CommandAPI.onEnable();
        instance = this;
        config = new PurpurConfig();
        Bukkit.getServicesManager().register(IModuleManager.class, new ModuleManager(), this, ServicePriority.High);

        new PurpurExtrasCommand();

        Bukkit.getServicesManager().getRegistration(IModuleManager.class).getProvider().reloadModules(config);
        config.saveConfig();
    }

    public static PurpurExtras getInstance() {
        return instance;
    }

    public void reloadPurpurExtrasConfig(CommandSender commandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            config = new PurpurConfig();
            Bukkit.getServicesManager().getRegistration(IModuleManager.class).getProvider().reloadModules(config);
            config.saveConfig();
            commandSender.sendMessage(Component.text("PurpurExtras configuration reloaded!"));
        });
    }

    public static NamespacedKey key(String string) {
        return new NamespacedKey(PurpurExtras.getInstance(), string);
    }

}
