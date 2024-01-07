package org.purpurmc.purpurextras.modules;

import me.youhavetrouble.entiddy.Entiddy;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.ArrayList;
import java.util.List;

/**
 * If enabled causes sheep named jeb_ to drop random colors of wool
 */
public class RandomColorJebSheepShearModule implements PurpurExtrasModule, Listener {

    private final List<Material> coloredWool = new ArrayList<>();

    protected RandomColorJebSheepShearModule() {
        this.coloredWool.add(Material.BLACK_WOOL);
        this.coloredWool.add(Material.BLUE_WOOL);
        this.coloredWool.add(Material.BROWN_WOOL);
        this.coloredWool.add(Material.CYAN_WOOL);
        this.coloredWool.add(Material.GRAY_WOOL);
        this.coloredWool.add(Material.GREEN_WOOL);
        this.coloredWool.add(Material.LIGHT_BLUE_WOOL);
        this.coloredWool.add(Material.LIGHT_GRAY_WOOL);
        this.coloredWool.add(Material.LIME_WOOL);
        this.coloredWool.add(Material.MAGENTA_WOOL);
        this.coloredWool.add(Material.ORANGE_WOOL);
        this.coloredWool.add(Material.PINK_WOOL);
        this.coloredWool.add(Material.PURPLE_WOOL);
        this.coloredWool.add(Material.RED_WOOL);
        this.coloredWool.add(Material.WHITE_WOOL);
        this.coloredWool.add(Material.YELLOW_WOOL);
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.mobs.sheep.jeb-shear-random-color", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJebSheepShear(PlayerShearEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!Entiddy.JEB_SHEEP.entiddy().isInstance(livingEntity)) return;
        List<ItemStack> loot = event.getDrops();
        for (ItemStack item : loot) {
            if (!item.getType().getKey().getKey().contains("_wool")) continue;
            item.setType(coloredWool.get((int) (Math.random() * coloredWool.size())));
        }
        event.setDrops(loot);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJebSheepShear(BlockShearEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!Entiddy.JEB_SHEEP.entiddy().isInstance(livingEntity)) return;
        List<ItemStack> loot = event.getDrops();
        for (ItemStack item : loot) {
            if (!item.getType().getKey().getKey().contains("_wool")) continue;
            item.setType(coloredWool.get((int) (Math.random() * coloredWool.size())));
        }
        event.setDrops(loot);
    }

}
