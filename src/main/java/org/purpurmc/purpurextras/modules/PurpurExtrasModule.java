package org.purpurmc.purpurextras.modules;

import org.purpurmc.purpurextras.PurpurExtras;
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
        modules.clear();
        HandlerList.unregisterAll(PurpurExtras.getInstance());

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
        modules.add(new LightningTransformsMobsModule());
        modules.add(new GrindstoneEnchantsBooksModule());
        modules.add(new SpawnerPlacementPermissionsModule());
        modules.add(new NetherBuildHeightModule());
        modules.add(new InvisibleItemFrameModule());
        modules.add(new UpgradeWoodToStoneToolsModule());
        modules.add(new UpgradeStoneToIronToolsModule());
        modules.add(new UpgradeIronToDiamondsToolsModule());
        modules.add(new DispenserBlocksModule());

        modules.forEach(module -> {
            if (module.shouldEnable()) {
                module.enable();
            }
        });

    }

}
