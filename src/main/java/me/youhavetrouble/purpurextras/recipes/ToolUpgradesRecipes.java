package me.youhavetrouble.purpurextras.recipes;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

public class ToolUpgradesRecipes {


    public static void addUpgradeRecipes(boolean woodToStone, boolean stoneToIron, boolean ironToDiamond) {

        SmithingRecipe recipe;

        if (woodToStone) {
            recipe = new SmithingRecipe(
                    key("pick_wood_to_stone"),
                    new ItemStack(Material.STONE_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.WOODEN_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("axe_wood_to_stone"),
                    new ItemStack(Material.STONE_AXE),
                    new RecipeChoice.MaterialChoice(Material.WOODEN_AXE),
                    new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("shovel_wood_to_stone"),
                    new ItemStack(Material.STONE_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.WOODEN_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("hoe_wood_to_stone"),
                    new ItemStack(Material.STONE_HOE),
                    new RecipeChoice.MaterialChoice(Material.WOODEN_HOE),
                    new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("sword_wood_to_stone"),
                    new ItemStack(Material.STONE_SWORD),
                    new RecipeChoice.MaterialChoice(Material.WOODEN_SWORD),
                    new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
            addRecipe(recipe);
        } else {
            removeRecipe("sword_wood_to_stone");
            removeRecipe("pick_wood_to_stone");
            removeRecipe("axe_wood_to_stone");
            removeRecipe("shovel_wood_to_stone");
            removeRecipe("hoe_wood_to_stone");
        }

        if (stoneToIron) {
            recipe = new SmithingRecipe(
                    key("pick_stone_to_iron"),
                    new ItemStack(Material.IRON_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.STONE_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("axe_stone_to_iron"),
                    new ItemStack(Material.IRON_AXE),
                    new RecipeChoice.MaterialChoice(Material.STONE_AXE),
                    new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("shovel_stone_to_iron"),
                    new ItemStack(Material.IRON_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.STONE_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("hoe_stone_to_iron"),
                    new ItemStack(Material.IRON_HOE),
                    new RecipeChoice.MaterialChoice(Material.STONE_HOE),
                    new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("sword_stone_to_iron"),
                    new ItemStack(Material.IRON_SWORD),
                    new RecipeChoice.MaterialChoice(Material.STONE_SWORD),
                    new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
            addRecipe(recipe);
        } else {
            removeRecipe("sword_stone_to_iron");
            removeRecipe("pick_stone_to_iron");
            removeRecipe("axe_stone_to_iron");
            removeRecipe("shovel_stone_to_iron");
            removeRecipe("hoe_stone_to_iron");
        }

        if (ironToDiamond) {
            recipe = new SmithingRecipe(
                    key("pick_iron_to_diamond"),
                    new ItemStack(Material.DIAMOND_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.IRON_PICKAXE),
                    new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("axe_iron_to_diamond"),
                    new ItemStack(Material.DIAMOND_AXE),
                    new RecipeChoice.MaterialChoice(Material.IRON_AXE),
                    new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("shovel_iron_to_diamond"),
                    new ItemStack(Material.DIAMOND_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.IRON_SHOVEL),
                    new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("hoe_iron_to_diamond"),
                    new ItemStack(Material.DIAMOND_HOE),
                    new RecipeChoice.MaterialChoice(Material.IRON_HOE),
                    new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
            addRecipe(recipe);
            recipe = new SmithingRecipe(
                    key("sword_iron_to_diamond"),
                    new ItemStack(Material.DIAMOND_SWORD),
                    new RecipeChoice.MaterialChoice(Material.IRON_SWORD),
                    new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
            addRecipe(recipe);
        } else {
            removeRecipe("sword_iron_to_diamond");
            removeRecipe("pick_iron_to_diamond");
            removeRecipe("axe_iron_to_diamond");
            removeRecipe("shovel_iron_to_diamond");
            removeRecipe("hoe_iron_to_diamond");
        }

    }

    private static NamespacedKey key(String string) {
        return new NamespacedKey(PurpurExtras.getInstance(), string);
    }

    private static void addRecipe(SmithingRecipe recipe) {
        if (Bukkit.getRecipe(recipe.getKey()) != null) return;
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.addRecipe(recipe));
    }

    private static void removeRecipe(String key) {
        Bukkit.getScheduler().runTask(PurpurExtras.getInstance(), () -> Bukkit.removeRecipe(new NamespacedKey(PurpurExtras.getInstance(), key)));
    }

}
