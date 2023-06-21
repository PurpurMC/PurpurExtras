package org.purpurmc.purpurextras.util;

import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.SmithingRecipe;

public class RecipeUtil {

    public static void addSmithingRecipe(SmithingRecipe recipe) {
        if (Bukkit.getRecipe(recipe.getKey()) != null) return;
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.addRecipe(recipe));
    }

    public static void removeRecipe(String key) {
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.removeRecipe(PurpurExtras.key(key)));
    }

}
