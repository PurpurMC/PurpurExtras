package org.purpurmc.purpurextras.modules.implementation;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * If speed-multiplier value is higher than 0, player will gain speed potion effect of the level of that value.
 * This only accepts integer values. Which blocks count as paths can be configured by listing them in path-blocks list.
 */
@ModuleInfo(name = "Path Speed Boost", description = "Get a speed boost level running on paths!")
public class RunFasterOnPaths extends PurpurExtrasModule {

    private final HashSet<Material> pathBlocks = new HashSet<>();
    private final int speedMultiplier;

    private final PotionEffect speedEffect;

    public RunFasterOnPaths(PurpurConfig config) {
        super(config);
        List<String> defaults = new ArrayList<>();
        defaults.add(Material.DIRT_PATH.toString());

        Logger logger = PurpurExtras.getInstance().getLogger();

        int rawSpeedMultiplier = getConfigInt("speed-multiplier", 0);
        speedMultiplier = Math.max(0, rawSpeedMultiplier);

        speedEffect = new PotionEffect(PotionEffectType.SPEED, 2, Math.max(speedMultiplier - 1, 0), false, false, false);

        List<String> rawPathBlocks = getConfigList("path-blocks", defaults);
        rawPathBlocks.forEach((string) -> {
            Material material = Material.getMaterial(string.toUpperCase(Locale.ENGLISH));
            if (material == null) {
                logger.warning(string + " is not a valid block material.");
                return;
            }
            pathBlocks.add(material);
        });
    }

    @Override
    public boolean shouldEnable() {
        return speedMultiplier > 0 && super.shouldEnable();
    }

    @Override
    public String getConfigPath() {
        return "settings.run-faster-on-paths";
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerMoveOnPath(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        Player player = event.getPlayer();
        if (player.isFlying()) return;
        if (player.isGliding()) return;
        if (player.isInsideVehicle()) return;

        Block block = player.getLocation().clone().subtract(0.0, 0.1, 0.0).getBlock();
        if (!pathBlocks.contains(block.getType())) return;

        player.addPotionEffect(speedEffect);
    }

}
