package me.youhavetrouble.purpurextras.modules;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.event.HandlerList;

import java.util.HashSet;

public interface PurpurExtrasModule {

    /**
     * Enables the feature, registers the listeners.
     */
    void enable();

    /**
     * @return true if the feature should be enabled
     */
    boolean shouldEnable();

    HashSet<PurpurExtrasModule> modules = new HashSet<>();

    static void reloadModules() {
        PurpurExtras.getInstance().reloadConfig();
        modules.clear();
        HandlerList.unregisterAll(PurpurExtras.getInstance());

        // TODO annotation scanner maybe

        modules.add(new BeeHiveLoreModule());
        modules.add(new AnvilChangesBlocksModule());
        modules.add(new ChorusFlowerAlwaysDropsModule());
        modules.add(new MobNoTargetModule());
        modules.add(new StonecutterDamageModule());
        modules.add(new ColoredBossBarsModule());
        modules.add(new RespawnAnchorNeedsChargeModule());
        modules.add(new VoidTotemModule());
        modules.add(new FurnaceBurnTimeModule());
        modules.add(new ForceNametaggedForRidingModule());
        modules.add(new EscapeCommandSlashModule());
        modules.add(new OpenIronDoorsWithHandModule());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });

    }

}
