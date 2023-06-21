package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetEvent;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If enabled, players having target.bypass.<mojang_mob_name> permission won't be targetted by that type of mob.
 */
@ModuleInfo(name = "Prevent Mob Targetting", description = "Prevents certain mobs from targetting players!")
public class MobNoTarget extends PurpurExtrasModule {

    public MobNoTarget(PurpurConfig config) {
        super(config);
    }

    @Override
    public String getConfigPath() {
        return "settings.use-notarget-permissions";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        if (!player.hasPermission("target.bypass." + event.getEntityType().getKey().getKey())) return;
        event.setCancelled(true);
    }
}
