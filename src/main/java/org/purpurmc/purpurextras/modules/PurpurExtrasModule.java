package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.event.HandlerList;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.*;

public interface PurpurExtrasModule {

    Reflections reflections = new Reflections("org.purpurmc.purpurextras.modules");

    /**
     * Enables the feature, registers the listeners.
     */
    void enable();

    /**
     * Registers permissions and checks config for if this module should be enabled.
     * @return true if the feature should be enabled
     */
    boolean shouldEnable();

    default boolean isPermissionRegistered(Permission permission) {
        String permName = permission.getName();
        return Bukkit.getServer().getPluginManager().getPermission(permName) != null;
    }

    static void reloadModules() {

        HandlerList.unregisterAll(PurpurExtras.getInstance());

        Set<Class<?>> subTypes = reflections.get(Scanners.SubTypes.of(PurpurExtrasModule.class).asClass());

        subTypes.forEach(clazz -> {
            try {
                PurpurExtrasModule module = (PurpurExtrasModule) clazz.getDeclaredConstructor().newInstance();
                if (module.shouldEnable()) {
                    module.enable();
                }
            } catch (Exception e) {
                PurpurExtras.getInstance().getSLF4JLogger().warn("Failed to load module {}", clazz.getSimpleName(), e);
            }
        });

    }
}
