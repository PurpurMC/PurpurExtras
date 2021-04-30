package me.youhavetrouble.purpurextras;

import me.youhavetrouble.purpurextras.config.PurpurConfig;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.forks.Yatopia;

import java.lang.reflect.InvocationTargetException;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static PurpurExtras instance;

    @Override
    public void onEnable() {
        try {
            Class.forName("net.pl3x.purpur.PurpurConfig");
        } catch (ClassNotFoundException e) {
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0PurpurExtras was created to compliment Purpur, and it appears you're not using it!"));
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0Purpur is a drop-in replacement for " + getServer().getName()+"."));
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0You can get Purpur on https://purpur.pl3x.net/downloads/"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Yatopia.unsafeYatopia()) {
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0PurpurExtras was created to compliment Purpur, and it appears you're using an unstable fork that doesn't contain all of its patches!"));
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0Purpur is a drop-in replacement for Yatopia."));
            getLogger().warning( ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0You can get Purpur on https://purpur.pl3x.net/downloads/"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        config = new PurpurConfig(this);
    }

    public static PurpurConfig getPurpurConfig() {
        return config;
    }
    public static PurpurExtras getInstance() {
        return instance;
    }

    public void registerListener(Class<?> clazz) {
        try {
            Listener listener = (org.bukkit.event.Listener) clazz.getConstructor().newInstance();
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
