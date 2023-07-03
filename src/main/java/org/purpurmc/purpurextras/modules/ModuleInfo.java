package org.purpurmc.purpurextras.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Establishes Module info for formatting through the PurpurExtras Command
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    /**
     * @return The name of the Module
     */
    String name();

    /**
     * @return A brief description of the module, to be used in a tooltip
     */
    String description();
}
