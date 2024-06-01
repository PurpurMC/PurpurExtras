package org.purpurmc.purpurextras.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows the player to Stop Drop N Roll to get extinguished from any flames.
 */
public class StopDropNRollModule implements PurpurExtrasModule, Listener {

    private final Map<Player, Boolean> playerLastSneakMap = new HashMap<>();;
    private double chance;
    private double amount;

    protected StopDropNRollModule() {}

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        chance = PurpurExtras.getPurpurConfig().getDouble("settings.stopdropandroll.chance", 0);
        amount = PurpurExtras.getPurpurConfig().getDouble("settings.stopdropandroll.amount", 0.5);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getDouble("settings.stopdropandroll.chance", 0) != 0;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        boolean isSneaking = event.isSneaking();

        if (player.getFireTicks() > 0 && isSneaking && !playerLastSneakMap.get(player)) {
            player.setFireTicks((int) (player.getFireTicks() * (1f - amount)));
        }

        playerLastSneakMap.put(player, isSneaking);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerLastSneakMap.put(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLastSneakMap.remove(event.getPlayer());
    }
}
