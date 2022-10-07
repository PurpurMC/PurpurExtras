package org.purpurmc.purpurextras.modules;

import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.listeners.ShieldCooldownListener;
import org.purpurmc.purpurextras.modules.listeners.ShieldDamageReductionListener;

public class ShieldSettingsModule implements PurpurExtrasModule, Listener {

    private static final double DEFAULT_SHIELD_DAMAGE_REDUCTION = 1.0;
    private static final int DEFAULT_SHIELD_COOLDOWN = 5 * 20;

    private double shieldDamageReduction = DEFAULT_SHIELD_DAMAGE_REDUCTION;
    private int shieldCooldown = DEFAULT_SHIELD_COOLDOWN;

    protected ShieldSettingsModule() {}

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        if (shieldDamageReduction != DEFAULT_SHIELD_DAMAGE_REDUCTION) {
            plugin.getServer().getPluginManager().registerEvents(new ShieldDamageReductionListener(shieldDamageReduction), plugin);
        }
        if (shieldCooldown != DEFAULT_SHIELD_COOLDOWN) {
            plugin.getServer().getPluginManager().registerEvents(new ShieldCooldownListener(shieldCooldown), plugin);
        }
    }

    @Override
    public boolean shouldEnable() {
        shieldDamageReduction = PurpurExtras.getPurpurConfig().getDouble("settings.shield.damage-reduction", shieldDamageReduction);
        shieldDamageReduction = Math.min(1.0, Math.max(0, shieldDamageReduction));

        shieldCooldown = PurpurExtras.getPurpurConfig().getInt("settings.shield.cooldown", shieldCooldown);
        shieldCooldown = Math.max(0, shieldCooldown);

        return shieldDamageReduction != DEFAULT_SHIELD_DAMAGE_REDUCTION || shieldCooldown != DEFAULT_SHIELD_COOLDOWN;
    }

}
