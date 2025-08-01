package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * If speed-multiplier value is higher than 0, player will gain speed potion effect of the level of that value.
 * This only accepts integer values. Which blocks count as paths can be configured by listing them in path-blocks list.
 */
public class RunFasterOnPathsModule implements PurpurExtrasModule, Listener {

    private final HashSet<Material> pathBlocks = new HashSet<>();
    private final AttributeModifier modifier;
    private final NamespacedKey attributeKey = new NamespacedKey(PurpurExtras.getInstance(), "run-faster-on-paths");

    protected RunFasterOnPathsModule() {
        List<String> defaults = new ArrayList<>();
        defaults.add(Material.DIRT_PATH.toString());

        PurpurConfig config = PurpurExtras.getPurpurConfig();
        Logger logger = PurpurExtras.getInstance().getLogger();

        String attributeModifierType = config.getString("settings.gameplay-settings.run-faster-on-paths.attribute-modifier-type", "add_scalar");
        AttributeModifier.Operation operation;
        try {
            operation = AttributeModifier.Operation.valueOf(attributeModifierType.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            logger.warning(attributeModifierType + " is not a valid attribute modifier type. Defaulting to add_scalar.");
            operation = AttributeModifier.Operation.ADD_SCALAR;
        }

        double value = config.getDouble("settings.gameplay-settings.run-faster-on-paths.value", 0d);

        modifier = new AttributeModifier(attributeKey, value, operation);

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
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        return config.getBoolean("settings.gameplay-settings.run-faster-on-paths.enabled", false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerMoveOnPath(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        Player player = event.getPlayer();
        if (player.isFlying()) return;
        if (player.isGliding()) return;
        if (player.isInsideVehicle()) return;

        Block block = player.getLocation().clone().subtract(0.0, 0.1, 0.0).getBlock();
        if (block.getType().isAir()) return;
        AttributeInstance attributeInstance = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attributeInstance == null) return;
        if (!pathBlocks.contains(block.getType())) {
            attributeInstance.removeModifier(attributeKey);
            return;
        }

        AttributeModifier existingModifier = attributeInstance.getModifier(attributeKey);

        if (existingModifier == null) {
            attributeInstance.addTransientModifier(this.modifier);
            return;
        }

        if (!existingModifier.getOperation().equals(this.modifier.getOperation()) && existingModifier.getAmount() != this.modifier.getAmount()) {
            attributeInstance.removeModifier(existingModifier);
            attributeInstance.addTransientModifier(this.modifier);
        }
    }

}
