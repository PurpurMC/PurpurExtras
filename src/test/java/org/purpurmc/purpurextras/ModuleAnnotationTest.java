package org.purpurmc.purpurextras;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import java.util.stream.Stream;

public class ModuleAnnotationTest {

    static Stream<Class<?>> getClasses() {
        return new Reflections("org.purpurmc.purpurextras.modules.implementation").get(Scanners.SubTypes.of(PurpurExtrasModule.class).asClass()).stream();
    }

    @ParameterizedTest
    @MethodSource("getClasses")
    void validModule(Class<?> input) {
        Assertions.assertTrue(PurpurExtrasModule.class.isAssignableFrom(input), "Class is not an instance of PurpurExtrasModule? (this should never happen)");
        Assertions.assertNotNull(input.getAnnotation(ModuleInfo.class), "Module " + input.getCanonicalName() + " is missing a ModuleInfo annotation!");
    }
}
