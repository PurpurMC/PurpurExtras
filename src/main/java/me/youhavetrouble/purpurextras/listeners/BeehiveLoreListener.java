package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeehiveLoreListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeehiveDrop(BlockBreakEvent event) {

        if (!event.getBlock().getType().equals(Material.BEE_NEST) && !event.getBlock().getType().equals(Material.BEEHIVE))
            return;

        Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand());

        event.setDropItems(false);
        Location location = event.getBlock().getLocation();
        Block block = event.getBlock();
        for (ItemStack item : drops) {
            if (item.getType().equals(Material.BEE_NEST) || item.getType().equals(Material.BEEHIVE)) {
                ItemMeta meta = item.getItemMeta();
                org.bukkit.block.Beehive beehive = (org.bukkit.block.Beehive) block.getState();
                Beehive beehiveData = (Beehive) block.getBlockData();
                Component beeCountComponent = Component.text().append(MiniMessage.markdown().parse(PurpurExtras.getPurpurConfig().beeHiveLoreBees, Template.of("bees", String.valueOf(beehive.getEntityCount())), Template.of("maxbees", String.valueOf(beehive.getMaxEntities()))).decoration(TextDecoration.ITALIC, false)).build();
                Component honeyLevelComponent = Component.text().append(MiniMessage.markdown().parse(PurpurExtras.getPurpurConfig().beeHiveLoreHoney, Template.of("honey", String.valueOf(beehiveData.getHoneyLevel())), Template.of("maxhoney", String.valueOf(beehiveData.getMaximumHoneyLevel()))).decoration(TextDecoration.ITALIC, false)).build();
                List<Component> lore = new ArrayList<>();
                lore.add(beeCountComponent);
                lore.add(honeyLevelComponent);
                meta.lore(lore);
                item.setItemMeta(meta);
                location.getWorld().dropItemNaturally(location, item);
                continue;
            }
            location.getWorld().dropItemNaturally(location, item);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeehiveCreativePick(InventoryCreativeEvent event) {
        if (!event.getCursor().getType().equals(Material.BEE_NEST) && !event.getCursor().getType().equals(Material.BEEHIVE))
            return;
        ItemStack item = event.getCursor();
        ItemMeta meta = item.getItemMeta();
        //TODO find a way to read bee data from ItemStack
        Component beeCountComponent = Component.text().append(MiniMessage.markdown().parse(PurpurExtras.getPurpurConfig().beeHiveLoreBees, Template.of("bees", String.valueOf(0)), Template.of("maxbees", String.valueOf(3))).decoration(TextDecoration.ITALIC, false)).build();
        Component honeyLevelComponent = Component.text().append(MiniMessage.markdown().parse(PurpurExtras.getPurpurConfig().beeHiveLoreHoney, Template.of("honey", String.valueOf(0)), Template.of("maxhoney", String.valueOf(5))).decoration(TextDecoration.ITALIC, false)).build();
        List<Component> lore = new ArrayList<>();
        lore.add(beeCountComponent);
        lore.add(honeyLevelComponent);
        meta.lore(lore);
        item.setItemMeta(meta);
    }

}
