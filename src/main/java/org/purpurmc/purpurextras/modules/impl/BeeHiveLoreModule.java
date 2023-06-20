package org.purpurmc.purpurextras.modules.impl;

import org.purpurmc.purpurextras.PurpurExtras;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.List;

/**
 * If true, will add lore with amount of bees and honey to the picked up hives.
 */
@ModuleInfo(name = "Bee Hives Lore", description = "Adds lore of hive information to dropped bee hives!")
public class BeeHiveLoreModule extends PurpurExtrasModule {

    private final String beeHiveLoreBees, beeHiveLoreHoney;
    private final MiniMessage miniMessage = PurpurExtras.getInstance().miniMessage;

    public BeeHiveLoreModule() {
        this.beeHiveLoreBees = getConfigString("bees", "<reset><gray>Bees: <bees>/<maxbees>");
        this.beeHiveLoreHoney = getConfigString("honey", "<reset><gray>Honey level: <honey>/<maxhoney>");
    }

    @Override
    public String getConfigPath() {
        return "settings.items.beehive-lore";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBeehiveDrop(BlockDropItemEvent event) {

        BlockState blockState = event.getBlockState();

        if (!blockState.getType().equals(Material.BEE_NEST) && !blockState.getType().equals(Material.BEEHIVE)) return;

        org.bukkit.block.Beehive beehive = (org.bukkit.block.Beehive) blockState;
        org.bukkit.block.data.type.Beehive beehiveData = (org.bukkit.block.data.type.Beehive) blockState.getBlockData();

        List<Item> itemDrops = event.getItems();

        for (Item itemDrop : itemDrops) {
            ItemStack item = itemDrop.getItemStack();
            if (item.getType().equals(Material.BEE_NEST) || item.getType().equals(Material.BEEHIVE)) {
                ItemMeta meta = item.getItemMeta();
                Component beeCountComponent = getBeesComponent(beehive.getEntityCount(), beehive.getMaxEntities());
                Component honeyLevelComponent = getHoneyComponent(beehiveData.getHoneyLevel(), beehiveData.getMaximumHoneyLevel());
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
        Component beeCountComponent = getBeesComponent(0, 3);
        Component honeyLevelComponent = getHoneyComponent(0, 5);
        List<Component> lore = List.of(beeCountComponent, honeyLevelComponent);
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private Component getBeesComponent(int bees, int maxBees) {
        String beeHiveLoreBeesString = beeHiveLoreBees;
        beeHiveLoreBeesString = beeHiveLoreBeesString.replaceAll("<bees>", String.valueOf(bees));
        beeHiveLoreBeesString = beeHiveLoreBeesString.replaceAll("<maxbees>", String.valueOf(maxBees));
        return miniMessage.deserializeOrNull(beeHiveLoreBeesString);
    }

    private Component getHoneyComponent(int honey, int maxHoney) {
        String beeHiveLoreHoneyString = beeHiveLoreHoney;
        beeHiveLoreHoneyString = beeHiveLoreHoneyString.replaceAll("<honey>", String.valueOf(honey));
        beeHiveLoreHoneyString = beeHiveLoreHoneyString.replaceAll("<maxhoney>", String.valueOf(maxHoney));
        return miniMessage.deserializeOrNull(beeHiveLoreHoneyString);
    }
}
