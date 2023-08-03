package org.purpurmc.purpurextras.modules;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.util.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

/**
 * Allows upgrading tools from wood to stone in the smithing table
 */
public class UpgradeWoodToStoneToolsModule implements PurpurExtrasModule {

    protected UpgradeWoodToStoneToolsModule() {
    }

    @Override
    public void enable() {
        SmithingTransformRecipe recipe;
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("pick_iron_to_diamond"), // key
                        new ItemStack(Material.DIAMOND_PICKAXE), // result
                        new RecipeChoice.MaterialChoice(Material.IRON_PICKAXE), // template
                        new RecipeChoice.MaterialChoice(Material.IRON_PICKAXE), // base
                        new RecipeChoice.MaterialChoice(Material.DIAMOND) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("axe_iron_to_diamond"), // key
                        new ItemStack(Material.DIAMOND_AXE), // result
                        new RecipeChoice.MaterialChoice(Material.IRON_AXE), // template
                        new RecipeChoice.MaterialChoice(Material.IRON_AXE), // base
                        new RecipeChoice.MaterialChoice(Material.DIAMOND) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("shovel_iron_to_diamond"), // key
                        new ItemStack(Material.DIAMOND_SHOVEL), // result
                        new RecipeChoice.MaterialChoice(Material.IRON_SHOVEL), // template
                        new RecipeChoice.MaterialChoice(Material.IRON_SHOVEL), // base
                        new RecipeChoice.MaterialChoice(Material.DIAMOND) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("hoe_iron_to_diamond"), // key
                        new ItemStack(Material.DIAMOND_HOE), // result
                        new RecipeChoice.MaterialChoice(Material.IRON_HOE), // template
                        new RecipeChoice.MaterialChoice(Material.IRON_HOE), // base
                        new RecipeChoice.MaterialChoice(Material.DIAMOND) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("sword_iron_to_diamond"), // key
                        new ItemStack(Material.DIAMOND_SWORD), // result
                        new RecipeChoice.MaterialChoice(Material.IRON_SWORD), // template
                        new RecipeChoice.MaterialChoice(Material.IRON_SWORD), // base
                        new RecipeChoice.MaterialChoice(Material.DIAMOND) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
    }

    @Override
    public boolean shouldEnable() {
        boolean shouldEnable = PurpurExtras.getPurpurConfig().getBoolean("settings.smithing-table.tools.wood-to-stone", false);
        if (shouldEnable) return true;
        RecipeUtil.removeRecipe("sword_wood_to_stone");
        RecipeUtil.removeRecipe("pick_wood_to_stone");
        RecipeUtil.removeRecipe("axe_wood_to_stone");
        RecipeUtil.removeRecipe("shovel_wood_to_stone");
        RecipeUtil.removeRecipe("hoe_wood_to_stone");
        return false;
    }

}
