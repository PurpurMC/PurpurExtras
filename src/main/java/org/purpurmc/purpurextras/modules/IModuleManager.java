package org.purpurmc.purpurextras.modules;

import org.purpurmc.purpurextras.PurpurConfig;

import java.util.Set;
import java.util.function.Predicate;

public interface IModuleManager {
    <T extends PurpurExtrasModule> T getModule(Class<T> module);

    Set<PurpurExtrasModule> getModules(Predicate<PurpurExtrasModule> predicate);

    void reloadModules(PurpurConfig config);
}
