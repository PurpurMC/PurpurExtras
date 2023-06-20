package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.List;
import java.util.Map;

public abstract class PurpurExtrasModule implements Listener {

    private boolean enabled = false;

    /**
     * Enables the feature, registers the listeners.
     */
    public void enable() {
        this.enabled = true;
        Bukkit.getPluginManager().registerEvents(this, PurpurExtras.getInstance());
    }

    /**
     * Disables the feature, primarily just unregistering listeners
     */
    public void disable() {
        this.enabled = false;
        HandlerList.unregisterAll(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return true if the feature should be enabled on startup
     */
    public boolean shouldEnable() {
        return getConfigBoolean("enabled", false);
    }

    public ModuleInfo anno() {
        return getClass().getAnnotation(ModuleInfo.class);
    }

    public abstract String getConfigPath();

    public ConfigurationSection getConfigSection(String section, Map<String, Object> def) {
        return PurpurExtras.getPurpurConfig().getConfigSection(getConfigPath() + "." + section, def);
    }

    public String getConfigString(String section, String def) {
        return PurpurExtras.getPurpurConfig().getString(getConfigPath() + "." + section, def);
    }

    public boolean getConfigBoolean(String section, boolean def) {
        return PurpurExtras.getPurpurConfig().getBoolean(getConfigPath() + "." + section, def);
    }

    public double getConfigDouble(String section, double def) {
        return PurpurExtras.getPurpurConfig().getDouble(getConfigPath() + "." + section, def);
    }

    public int getConfigInt(String section, int def) {
        return PurpurExtras.getPurpurConfig().getInt(getConfigPath() + "." + section, def);
    }

    public List<String> getConfigList(String section, List<String> def) {
        return PurpurExtras.getPurpurConfig().getList(getConfigPath() + "." + section, def);
    }

}
