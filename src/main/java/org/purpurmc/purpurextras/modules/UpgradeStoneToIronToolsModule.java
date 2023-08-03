package org.purpurmc.purpurextras.modules;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.util.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

/**
 * Allows upgrading tools from stone to iron in the smithing table
 */
public class UpgradeStoneToIronToolsModule implements PurpurExtrasModule {

    protected UpgradeStoneToIronToolsModule() {
    }

    @Override
    public void enable() {
        SmithingTransformRecipe recipe;
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("pick_stone_to_iron"), // key
                        new ItemStack(Material.IRON_PICKAXE), // result
                        new RecipeChoice.MaterialChoice(Material.STONE_PICKAXE), // template
                        new RecipeChoice.MaterialChoice(Material.STONE_PICKAXE), // base
                        new RecipeChoice.MaterialChoice(Material.IRON_INGOT) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("axe_stone_to_iron"), // key
                        new ItemStack(Material.IRON_AXE), // result
                        new RecipeChoice.MaterialChoice(Material.STONE_AXE), // template
                        new RecipeChoice.MaterialChoice(Material.STONE_AXE), // base
                        new RecipeChoice.MaterialChoice(Material.IRON_INGOT) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("shovel_stone_to_iron"), // key
                        new ItemStack(Material.IRON_SHOVEL), // result
                        new RecipeChoice.MaterialChoice(Material.STONE_SHOVEL), // template
                        new RecipeChoice.MaterialChoice(Material.STONE_SHOVEL), // base
                        new RecipeChoice.MaterialChoice(Material.IRON_INGOT) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("hoe_stone_to_iron"), // key
                        new ItemStack(Material.IRON_HOE), // result
                        new RecipeChoice.MaterialChoice(Material.STONE_HOE), // template
                        new RecipeChoice.MaterialChoice(Material.STONE_HOE), // base
                        new RecipeChoice.MaterialChoice(Material.IRON_INGOT) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
        recipe =
                new SmithingTransformRecipe(
                        PurpurExtras.key("sword_stone_to_iron"), // key
                        new ItemStack(Material.IRON_SWORD), // result
                        new RecipeChoice.MaterialChoice(Material.STONE_SWORD), // template
                        new RecipeChoice.MaterialChoice(Material.STONE_SWORD), // base
                        new RecipeChoice.MaterialChoice(Material.IRON_INGOT) // addition
                );
        RecipeUtil.addSmithingRecipe(recipe);
    }

    @Override
    public boolean shouldEnable() {
        boolean shouldEnable = PurpurExtras.getPurpurConfig().getBoolean("settings.smithing-table.tools.stone-to-iron", false);
        if (shouldEnable) return true;
        RecipeUtil.removeRecipe("sword_stone_to_iron");
        RecipeUtil.removeRecipe("pick_stone_to_iron");
        RecipeUtil.removeRecipe("axe_stone_to_iron");
        RecipeUtil.removeRecipe("shovel_stone_to_iron");
        RecipeUtil.removeRecipe("hoe_stone_to_iron");
        return false;
    }

}
