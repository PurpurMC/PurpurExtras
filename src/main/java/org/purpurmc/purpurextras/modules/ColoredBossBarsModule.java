package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ColoredBossBarsModule implements PurpurExtrasModule, Listener {

    private final NamespacedKey dyeColor = PurpurExtras.key("dyedColor");

    protected ColoredBossBarsModule() {}
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.dye-boss-bars", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void onBossBarDye(PlayerInteractEntityEvent event){
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if(!(event.getRightClicked() instanceof Boss bossClicked)) return;
        Player player = event.getPlayer();
        String materialName = player.getInventory().getItemInMainHand().getType().toString();
        if (!materialName.contains("_DYE")) return;
        int index = materialName.indexOf("_DYE");
        String bossBarColor = materialName.substring(0, index);
        try {
            BarColor.valueOf(bossBarColor);
        } catch (IllegalArgumentException e) {
            return;
        }
        bossClicked.getBossBar().setColor(BarColor.valueOf(bossBarColor));
        PersistentDataContainer pdc = bossClicked.getPersistentDataContainer();
        pdc.set(dyeColor, PersistentDataType.STRING, bossBarColor);
    }


    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onBossBarDyeOnLoad(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Boss boss)) return;
        PersistentDataContainer pdc = boss.getPersistentDataContainer();
        if (!pdc.has(dyeColor, PersistentDataType.STRING)) return;
        String color = pdc.get(dyeColor, PersistentDataType.STRING);
        boss.getBossBar().setColor(BarColor.valueOf(color));
    }
}
