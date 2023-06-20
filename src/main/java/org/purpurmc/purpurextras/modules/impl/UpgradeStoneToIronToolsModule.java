package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;
import org.purpurmc.purpurextras.util.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

/**
 * Allows upgrading tools from stone to iron in the smithing table
 */
public class UpgradeStoneToIronToolsModule implements PurpurExtrasModule {

    @Override
    public void enable() {
        SmithingRecipe recipe;
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("pick_stone_to_iron"),
                new ItemStack(Material.IRON_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.STONE_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("axe_stone_to_iron"),
                new ItemStack(Material.IRON_AXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.STONE_AXE),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("shovel_stone_to_iron"),
                new ItemStack(Material.IRON_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.STONE_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("hoe_stone_to_iron"),
                new ItemStack(Material.IRON_HOE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.STONE_HOE),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("sword_stone_to_iron"),
                new ItemStack(Material.IRON_SWORD),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.STONE_SWORD),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT), true);
        RecipeUtil.addSmithingRecipe(recipe);
    }

    @Override
    public boolean shouldEnable() {
        boolean shouldEnable = getConfigBoolean("settings.smithing-table.tools.stone-to-iron", false);
        if (shouldEnable) return true;
        RecipeUtil.removeRecipe("sword_stone_to_iron");
        RecipeUtil.removeRecipe("pick_stone_to_iron");
        RecipeUtil.removeRecipe("axe_stone_to_iron");
        RecipeUtil.removeRecipe("shovel_stone_to_iron");
        RecipeUtil.removeRecipe("hoe_stone_to_iron");
        return false;
    }

    @Override
    public String getConfigPath() {
        return "settings.smithing-table.tools.stone-to-iron";
    }

}
