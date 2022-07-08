package me.youhavetrouble.purpurextras.util;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.SmithingRecipe;

public class RecipeUtil {

    public static void addRecipe(SmithingRecipe recipe) {
        if (Bukkit.getRecipe(recipe.getKey()) != null) return;
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.addRecipe(recipe));
    }

    public static void removeRecipe(String key) {
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.removeRecipe(new NamespacedKey(PurpurExtras.getInstance(), key)));
    }

}
