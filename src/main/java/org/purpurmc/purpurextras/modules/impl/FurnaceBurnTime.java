package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If enabled, multiplier field will be used to modify fuel burn time in furnaces.
 */
@ModuleInfo(name = "Furnace Burn Time", description = "Modify the burn time of furnaces!")
public class FurnaceBurnTime extends PurpurExtrasModule {

    private final double furnaceBurnTimeMultiplier;

    public FurnaceBurnTime() {
        furnaceBurnTimeMultiplier = getConfigDouble("multiplier", 1.0);
    }

    @Override
    public boolean shouldEnable() {
        return super.shouldEnable() && furnaceBurnTimeMultiplier != 1.0;
    }

    @Override
    public String getConfigPath() {
        return "settings.furnace.burn-time";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        int burnTime = event.getBurnTime();
        event.setBurnTime((int) (burnTime * furnaceBurnTimeMultiplier));
    }
}
