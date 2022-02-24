package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FurnaceBurnTimeListener implements Listener {

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        int burnTime = event.getBurnTime();
        event.setBurnTime((int) (burnTime * PurpurExtras.getPurpurConfig().furnaceBurnTimeMultiplier));
    }
}
