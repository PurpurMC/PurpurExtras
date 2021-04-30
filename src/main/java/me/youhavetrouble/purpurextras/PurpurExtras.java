package me.youhavetrouble.purpurextras;

import me.youhavetrouble.purpurextras.config.PurpurConfig;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.InvocationTargetException;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static PurpurExtras instance;

    @Override
    public void onEnable() {
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
