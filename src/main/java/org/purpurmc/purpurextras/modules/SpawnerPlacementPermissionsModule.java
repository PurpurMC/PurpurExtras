package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

public class SpawnerPlacementPermissionsModule implements PurpurExtrasModule, Listener {

    protected SpawnerPlacementPermissionsModule() {}

    private final String spawnerPlacePermission = "purpurextras.spawnerplace";
    private final Map<String, Boolean> mobSpawners = new HashMap<String, Boolean>();

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        addSpawnerPermissions();
        registerPermission(spawnerPlacePermission, "Allows player to place spawner", PermissionDefault.OP, mobSpawners);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.spawner-placement-requires-specific-pemission", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        String entityType;
        String entityTypePermission;
        if (event.getBlock().getState(false) instanceof CreatureSpawner spawner) {
            entityType = String.valueOf(spawner.getSpawnedType()).toLowerCase(Locale.ROOT);
            entityTypePermission = ("." + entityType);
        } else {
            return;
        }
        if (player.hasPermission( spawnerPlacePermission + entityTypePermission)) return;
        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to place a <spawner> spawner!", Placeholder.unparsed("spawner", entityType)));
    }


    private void addSpawnerPermissions(){
        mobSpawners.put((spawnerPlacePermission + ".allay"), true);
        mobSpawners.put((spawnerPlacePermission + ".axolotl"), true);
        mobSpawners.put((spawnerPlacePermission + ".bat"), true);
        mobSpawners.put((spawnerPlacePermission + ".bee"), true);
        mobSpawners.put((spawnerPlacePermission + ".blaze"), true);
        mobSpawners.put((spawnerPlacePermission + ".cat"), true);
        mobSpawners.put((spawnerPlacePermission + ".cavespider"), true);
        mobSpawners.put((spawnerPlacePermission + ".chicken"), true);
        mobSpawners.put((spawnerPlacePermission + ".cod"), true);
        mobSpawners.put((spawnerPlacePermission + ".cow"), true);
        mobSpawners.put((spawnerPlacePermission + ".creeper"), true);
        mobSpawners.put((spawnerPlacePermission + ".dolphin"), true);
        mobSpawners.put((spawnerPlacePermission + ".donkey"), true);
        mobSpawners.put((spawnerPlacePermission + ".drowned"), true);
        mobSpawners.put((spawnerPlacePermission + ".elderguardian"), true);
        mobSpawners.put((spawnerPlacePermission + ".enderman"), true);
        mobSpawners.put((spawnerPlacePermission + ".endermite"), true);
        mobSpawners.put((spawnerPlacePermission + ".evoker"), true);
        mobSpawners.put((spawnerPlacePermission + ".fox"), true);
        mobSpawners.put((spawnerPlacePermission + ".frog"), true);
        mobSpawners.put((spawnerPlacePermission + ".ghast"), true);
        mobSpawners.put((spawnerPlacePermission + ".glowsquid"), true);
        mobSpawners.put((spawnerPlacePermission + ".goat"), true);
        mobSpawners.put((spawnerPlacePermission + ".guardian"), true);
        mobSpawners.put((spawnerPlacePermission + ".hoglin"), true);
        mobSpawners.put((spawnerPlacePermission + ".horse"), true);
        mobSpawners.put((spawnerPlacePermission + ".husk"), true);
        mobSpawners.put((spawnerPlacePermission + ".illusioner"), true);
        mobSpawners.put((spawnerPlacePermission + ".irongolem"), true);
        mobSpawners.put((spawnerPlacePermission + ".llama"), true);
        mobSpawners.put((spawnerPlacePermission + ".magmacube"), true);
        mobSpawners.put((spawnerPlacePermission + ".mule"), true);
        mobSpawners.put((spawnerPlacePermission + ".mushroomcow"), true);
        mobSpawners.put((spawnerPlacePermission + ".ocelot"), true);
        mobSpawners.put((spawnerPlacePermission + ".panda"), true);
        mobSpawners.put((spawnerPlacePermission + ".parrot"), true);
        mobSpawners.put((spawnerPlacePermission + ".phantom"), true);
        mobSpawners.put((spawnerPlacePermission + ".pig"), true);
        mobSpawners.put((spawnerPlacePermission + ".piglin"), true);
        mobSpawners.put((spawnerPlacePermission + ".piglinbrute"), true);
        mobSpawners.put((spawnerPlacePermission + ".pigzombie"), true);
        mobSpawners.put((spawnerPlacePermission + ".pillager"), true);
        mobSpawners.put((spawnerPlacePermission + ".polarbear"), true);
        mobSpawners.put((spawnerPlacePermission + ".pufferfish"), true);
        mobSpawners.put((spawnerPlacePermission + ".rabbit"), true);
        mobSpawners.put((spawnerPlacePermission + ".ravager"), true);
        mobSpawners.put((spawnerPlacePermission + ".salmon"), true);
        mobSpawners.put((spawnerPlacePermission + ".sheep"), true);
        mobSpawners.put((spawnerPlacePermission + ".shulker"), true);
        mobSpawners.put((spawnerPlacePermission + ".silverfish"), true);
        mobSpawners.put((spawnerPlacePermission + ".skeleton"), true);
        mobSpawners.put((spawnerPlacePermission + ".skeletonhorse"), true);
        mobSpawners.put((spawnerPlacePermission + ".slime"), true);
        mobSpawners.put((spawnerPlacePermission + ".snowman"), true);
        mobSpawners.put((spawnerPlacePermission + ".spider"), true);
        mobSpawners.put((spawnerPlacePermission + ".squid"), true);
        mobSpawners.put((spawnerPlacePermission + ".stray"), true);
        mobSpawners.put((spawnerPlacePermission + ".strider"), true);
        mobSpawners.put((spawnerPlacePermission + ".tadpole"), true);
        mobSpawners.put((spawnerPlacePermission + ".traderllama"), true);
        mobSpawners.put((spawnerPlacePermission + ".tropicalfish"), true);
        mobSpawners.put((spawnerPlacePermission + ".turtle"), true);
        mobSpawners.put((spawnerPlacePermission + ".vex"), true);
        mobSpawners.put((spawnerPlacePermission + ".villager"), true);
        mobSpawners.put((spawnerPlacePermission + ".vindicator"), true);
        mobSpawners.put((spawnerPlacePermission + ".wanderingtrader"), true);
        mobSpawners.put((spawnerPlacePermission + ".warden"), true);
        mobSpawners.put((spawnerPlacePermission + ".witch"), true);
        mobSpawners.put((spawnerPlacePermission + ".wither"), true);
        mobSpawners.put((spawnerPlacePermission + ".witherskeleton"), true);
        mobSpawners.put((spawnerPlacePermission + ".witherskull"), true);
        mobSpawners.put((spawnerPlacePermission + ".wolf"), true);
        mobSpawners.put((spawnerPlacePermission + ".zoglin"), true);
        mobSpawners.put((spawnerPlacePermission + ".zombie"), true);
        mobSpawners.put((spawnerPlacePermission + ".zombiehorse"), true);
        mobSpawners.put((spawnerPlacePermission + ".zombievillager"), true);
    }
}
