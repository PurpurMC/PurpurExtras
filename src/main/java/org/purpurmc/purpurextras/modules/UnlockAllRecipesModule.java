package org.purpurmc.purpurextras.modules;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.Iterator;

/**
 * Unlocks all available recipes on join.
 * Players can be exempt from this by denying them purpurextras.unlockallrecipesonjoin permission.
 */
public class UnlockAllRecipesModule implements PurpurExtrasModule, Listener {

    private final Permission unlockRecipesPermission = new Permission("purpurextras.unlockallrecipesonjoin", "Players with this permission will have all recipes unlocked upon login if that feature is enabled in the config",
            PermissionDefault.TRUE);

    protected UnlockAllRecipesModule() {}
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public boolean shouldEnable() {
        registerPermissions(unlockRecipesPermission);
        return PurpurExtras.getPurpurConfig().getBoolean("settings.unlock-all-recipes-on-join", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoinRecipeUnlock(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(unlockRecipesPermission)) return;
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (!(recipe instanceof Keyed keyedRecipe)) continue;
            player.discoverRecipe(keyedRecipe.getKey());
        }
    }

}
