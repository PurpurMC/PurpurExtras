package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.List;
import java.util.Map;

public abstract class PurpurExtrasModule implements Listener {

    private boolean enabled = false;
    protected PurpurConfig config;

    public PurpurExtrasModule(PurpurConfig config) {
        this.config = config;
    }

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
        return config.getConfigSection(getConfigPath() + "." + section, def);
    }

    public String getConfigString(String section, String def) {
        return config.getString(getConfigPath() + "." + section, def);
    }

    public boolean getConfigBoolean(String section, boolean def) {
        return config.getBoolean(getConfigPath() + "." + section, def);
    }

    public double getConfigDouble(String section, double def) {
        return config.getDouble(getConfigPath() + "." + section, def);
    }

    public int getConfigInt(String section, int def) {
        return config.getInt(getConfigPath() + "." + section, def);
    }

    public List<String> getConfigList(String section, List<String> def) {
        return config.getList(getConfigPath() + "." + section, def);
    }

}
