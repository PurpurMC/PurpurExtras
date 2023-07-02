package org.purpurmc.purpurextras.modules;

import org.bukkit.event.HandlerList;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleManager implements IModuleManager {

    private static final Reflections reflections = new Reflections("org.purpurmc.purpurextras.modules.implementation");
    private final Set<PurpurExtrasModule> modules;

    public ModuleManager() {
        modules = new HashSet<>();
    }

    public <T extends PurpurExtrasModule> T getModule(Class<T> module) {
        return (T) modules.stream().filter(c -> module.isAssignableFrom(c.getClass())).findFirst().orElse(null);
    }

    public Set<PurpurExtrasModule> getModules(Predicate<PurpurExtrasModule> predicate) {
        return modules.stream().filter(predicate).collect(Collectors.toCollection(HashSet::new));
    }

    public void reloadModules(PurpurConfig config) {

        HandlerList.unregisterAll(PurpurExtras.getInstance());
        modules.clear();
        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(ModuleInfo.class);

        subTypes.forEach(clazz -> {
            try {
                PurpurExtrasModule module = (PurpurExtrasModule) clazz.getDeclaredConstructor(PurpurConfig.class).newInstance(config);
                if (module.shouldEnable()) {
                    PurpurExtras.getInstance().getLogger().info("Registered module " + module.anno().name());
                    module.enable();
                }
                modules.add(module);
            } catch (Exception e) {
                PurpurExtras.getInstance().getSLF4JLogger().warn("Failed to load module " + clazz.getSimpleName(), e);
            }
        });

    }
}
