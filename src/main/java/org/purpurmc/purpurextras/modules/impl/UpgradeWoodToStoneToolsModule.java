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
 * Allows upgrading tools from wood to stone in the smithing table
 */
public class UpgradeWoodToStoneToolsModule implements PurpurExtrasModule {

    @Override
    public void enable() {
        SmithingRecipe recipe;
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("pick_wood_to_stone"),
                new ItemStack(Material.STONE_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.WOODEN_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("axe_wood_to_stone"),
                new ItemStack(Material.STONE_AXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.WOODEN_AXE),
                new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("shovel_wood_to_stone"),
                new ItemStack(Material.STONE_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.WOODEN_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("hoe_wood_to_stone"),
                new ItemStack(Material.STONE_HOE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.WOODEN_HOE),
                new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("sword_wood_to_stone"),
                new ItemStack(Material.STONE_SWORD),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.WOODEN_SWORD),
                new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.BLACKSTONE), true);
        RecipeUtil.addSmithingRecipe(recipe);
    }

    @Override
    public boolean shouldEnable() {
        boolean shouldEnable = getConfigBoolean("settings.smithing-table.tools.wood-to-stone", false);
        if (shouldEnable) return true;
        RecipeUtil.removeRecipe("sword_wood_to_stone");
        RecipeUtil.removeRecipe("pick_wood_to_stone");
        RecipeUtil.removeRecipe("axe_wood_to_stone");
        RecipeUtil.removeRecipe("shovel_wood_to_stone");
        RecipeUtil.removeRecipe("hoe_wood_to_stone");
        return false;
    }

    @Override
    public String getConfigPath() {
        return "";
    }

}
