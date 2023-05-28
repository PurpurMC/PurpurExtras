package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class RunFasterOnPathsModule implements PurpurExtrasModule, Listener {

    private final HashSet<Material> pathBlocks = new HashSet<>();
    private final int speedMultiplier;

    private final PotionEffect speedEffect;

    protected RunFasterOnPathsModule() {
        List<String> defaults = new ArrayList<>();
        defaults.add(Material.DIRT_PATH.toString());

        PurpurConfig config = PurpurExtras.getPurpurConfig();
        Logger logger = PurpurExtras.getInstance().getLogger();

        int rawSpeedMultiplier = config.getInt("settings.gameplay-settings.run-faster-on-paths.speed-multiplier", 0);
        speedMultiplier = Math.max(0, rawSpeedMultiplier);

        speedEffect = new PotionEffect(PotionEffectType.SPEED, 2, Math.max(speedMultiplier - 1, 0), false, false, false);

        List<String> rawPathBlocks = config.getList("settings.gameplay-settings.run-faster-on-paths.path-blocks", defaults);
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
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return speedMultiplier > 0;
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
