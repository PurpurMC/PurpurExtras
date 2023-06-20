package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.List;
import java.util.Map;

public interface PurpurExtrasModule extends Listener {

    /**
     * Enables the feature, registers the listeners.
     */
    default void enable() {
        Bukkit.getPluginManager().registerEvents(this, PurpurExtras.getInstance());
    }

    /**
     * Disables the feature, primarily just unregistering listeners
     */
    default void disable() {
        HandlerList.unregisterAll(this);
    }

    /**
     * @return true if the feature should be enabled on startup
     */
    default boolean shouldEnable() {
        return getConfigBoolean("enabled", false);
    }

    default ModuleInfo anno() {
        return getClass().getAnnotation(ModuleInfo.class);
    }

    String getConfigPath();

    default ConfigurationSection getConfigSection(String section, Map<String, Object> def) {
        return PurpurExtras.getPurpurConfig().getConfigSection(section, def);
    }

    default String getConfigString(String section, String def) {
        return PurpurExtras.getPurpurConfig().getString(section, def);
    }

    default boolean getConfigBoolean(String section, boolean def) {
        return PurpurExtras.getPurpurConfig().getBoolean(getConfigPath() + "." + section, def);
    }

    default double getConfigDouble(String section, double def) {
        return PurpurExtras.getPurpurConfig().getDouble(section, def);
    }

    default int getConfigInt(String section, int def) {
        return PurpurExtras.getPurpurConfig().getInt(section, def);
    }

    default List<String> getConfigList(String section, List<String> def) {
        return PurpurExtras.getPurpurConfig().getList(section, def);
    }

}
