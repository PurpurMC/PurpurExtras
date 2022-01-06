package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class BeehiveLoreListener implements Listener {

    private final MiniMessage miniMessage = PurpurExtras.getInstance().miniMessage;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeehiveDrop(BlockDropItemEvent event) {

        BlockState blockState = event.getBlockState();

        if (!blockState.getType().equals(Material.BEE_NEST)
                && !blockState.getType().equals(Material.BEEHIVE))
            return;

        org.bukkit.block.Beehive beehive = (org.bukkit.block.Beehive) blockState;
        org.bukkit.block.data.type.Beehive beehiveData = (org.bukkit.block.data.type.Beehive) blockState.getBlockData();

        List<Item> itemDrops = event.getItems();

        for (Item itemDrop : itemDrops) {
            ItemStack item = itemDrop.getItemStack();
            if (item.getType().equals(Material.BEE_NEST) || item.getType().equals(Material.BEEHIVE)) {
                ItemMeta meta = item.getItemMeta();
                Component beeCountComponent = Component.text().append(miniMessage.parse(PurpurExtras.getPurpurConfig().beeHiveLoreBees, Template.of("bees", String.valueOf(beehive.getEntityCount())), Template.of("maxbees", String.valueOf(beehive.getMaxEntities()))).decoration(TextDecoration.ITALIC, false)).build();
                Component honeyLevelComponent = Component.text().append(miniMessage.parse(PurpurExtras.getPurpurConfig().beeHiveLoreHoney, Template.of("honey", String.valueOf(beehiveData.getHoneyLevel())), Template.of("maxhoney", String.valueOf(beehiveData.getMaximumHoneyLevel()))).decoration(TextDecoration.ITALIC, false)).build();
                List<Component> lore = List.of(beeCountComponent, honeyLevelComponent);
                meta.lore(lore);
                item.setItemMeta(meta);
                itemDrop.setItemStack(item);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeehiveCreativePick(InventoryCreativeEvent event) {
        if (!event.getCursor().getType().equals(Material.BEE_NEST)
                && !event.getCursor().getType().equals(Material.BEEHIVE))
            return;
        ItemStack item = event.getCursor();
        ItemMeta meta = item.getItemMeta();
        //TODO find a way to read bee data from ItemStack
        Component beeCountComponent = Component.text().append(miniMessage.parse(PurpurExtras.getPurpurConfig().beeHiveLoreBees, Template.of("bees", String.valueOf(0)), Template.of("maxbees", String.valueOf(3))).decoration(TextDecoration.ITALIC, false)).build();
        Component honeyLevelComponent = Component.text().append(miniMessage.parse(PurpurExtras.getPurpurConfig().beeHiveLoreHoney, Template.of("honey", String.valueOf(0)), Template.of("maxhoney", String.valueOf(5))).decoration(TextDecoration.ITALIC, false)).build();
        List<Component> lore = List.of(beeCountComponent, honeyLevelComponent);
        meta.lore(lore);
        item.setItemMeta(meta);
    }

}
