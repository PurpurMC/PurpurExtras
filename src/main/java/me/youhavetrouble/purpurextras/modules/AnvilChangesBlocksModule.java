package me.youhavetrouble.purpurextras.modules;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.PurpurConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class AnvilChangesBlocksModule implements PurpurExtrasModule, Listener {

    private final HashSet<Material> anvils = new HashSet<>();
    private final HashMap<Material, Material> anvilCrushBlocksIndex = new HashMap<>();
    protected AnvilChangesBlocksModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        Map<String, Object> defaults = new HashMap<>();

        anvils.add(Material.ANVIL);
        anvils.add(Material.CHIPPED_ANVIL);
        anvils.add(Material.DAMAGED_ANVIL);

        defaults.put("cobblestone", "sand");
        ConfigurationSection section = config.getConfigSection("settings.anvil-crushes-blocks.blocks", defaults);
        Logger logger = PurpurExtras.getInstance().getLogger();
        for (String key : section.getKeys(false)) {
            String matString = section.getString(key);
            if (matString == null) continue;
            Material materialFrom = Material.getMaterial(key.toUpperCase(Locale.ENGLISH));

            if (materialFrom == null || !materialFrom.isBlock()) {
                logger.warning(key + " is not valid block material.");
                continue;
            }
            Material materialTo = Material.getMaterial(matString.toUpperCase());
            if (materialTo == null || !materialTo.isBlock()) {
                logger.warning(matString + " is not valid block material.");
                continue;
            }
            anvilCrushBlocksIndex.put(materialFrom, materialTo);
        }

    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.anvil-crushes-blocks.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilDrop(EntityChangeBlockEvent event) {
        if (!anvils.contains(event.getTo())) return;

        Location belowAnvil = event.getBlock().getLocation().clone().subtract(0, 1, 0);
        Block blockBelowAnvil = belowAnvil.getBlock();

        if (!anvilCrushBlocksIndex.containsKey(blockBelowAnvil.getType())) return;

        blockBelowAnvil.setType(anvilCrushBlocksIndex.get(blockBelowAnvil.getType()), true);
    }
}
