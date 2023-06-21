package org.purpurmc.purpurextras.modules;

import org.bukkit.event.HandlerList;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModuleManager {

    private static final Reflections reflections = new Reflections("org.purpurmc.purpurextras.modules");
    private final List<PurpurExtrasModule> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
    }

    public PurpurExtrasModule getModule(Class<? extends PurpurExtrasModule> module) {
        return modules.stream().filter(c -> module.isAssignableFrom(c.getClass())).findFirst().orElse(null);
    }

    public void reloadModules(PurpurConfig config) {

        HandlerList.unregisterAll(PurpurExtras.getInstance());

        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(ModuleInfo.class);

        subTypes.forEach(clazz -> {
            try {
                PurpurExtrasModule module = (PurpurExtrasModule) clazz.getDeclaredConstructor(PurpurConfig.class).newInstance(config);
                if (module.shouldEnable()) {
                    module.enable();
                }
                modules.add(module);
            } catch (Exception e) {
                PurpurExtras.getInstance().getSLF4JLogger().warn("Failed to load module " + clazz.getSimpleName(), e);
            }
        });

    }
}
