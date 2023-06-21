package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.Iterator;

/**
 * Unlocks all available recipes on join.
 * Players can be exempt from this by denying them purpurextras.unlockallrecipesonjoin permission.
 */
@ModuleInfo(name = "Unlock All Recipes", description = "Unlock all crafting recipes on join!")
public class UnlockAllRecipes extends PurpurExtrasModule {

    private final String permission = "purpurextras.unlockallrecipesonjoin";

    public UnlockAllRecipes() {
        DefaultPermissions.registerPermission(
                permission,
                "Players with this permission will have all recipes unlocked upon login if that feature is enabled in the config",
                PermissionDefault.TRUE
        );
    }

    @Override
    public String getConfigPath() {
        return "settings.unlock-all-recipes-on-join";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoinRecipeUnlock(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(permission)) return;
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (!(recipe instanceof Keyed keyedRecipe)) continue;
            player.discoverRecipe(keyedRecipe.getKey());
        }
    }

}
