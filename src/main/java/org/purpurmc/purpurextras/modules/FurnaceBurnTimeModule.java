package org.purpurmc.purpurextras.modules;

import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FurnaceBurnTimeModule implements PurpurExtrasModule, Listener {

    private final double furnaceBurnTimeMultiplier;

    protected FurnaceBurnTimeModule() {
        furnaceBurnTimeMultiplier = PurpurExtras.getPurpurConfig().getDouble("settings.furnace.burn-time.multiplier", 1.0);
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (!PurpurExtras.getPurpurConfig().getBoolean("settings.furnace.burn-time.enabled", false)) return false;
        return furnaceBurnTimeMultiplier != 1.0;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        int burnTime = event.getBurnTime();
        event.setBurnTime((int) (burnTime * furnaceBurnTimeMultiplier));
    }
}
