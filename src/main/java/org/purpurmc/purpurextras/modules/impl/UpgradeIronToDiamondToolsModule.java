package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.inventory.SmithingTransformRecipe;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;
import org.purpurmc.purpurextras.util.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

/**
 * Allows upgrading tools from iron to diamond in the smithing table
 */
@ModuleInfo(name = "Upgrade Iron to Diamond", description = "Allows for upgrading from iron to diamond tools in the smithing table")
public class UpgradeIronToDiamondToolsModule extends PurpurExtrasModule {

    @Override
    public void enable() {
        SmithingRecipe recipe;
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("pick_iron_to_diamond"),
                new ItemStack(Material.DIAMOND_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.IRON_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("axe_iron_to_diamond"),
                new ItemStack(Material.DIAMOND_AXE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.IRON_AXE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("shovel_iron_to_diamond"),
                new ItemStack(Material.DIAMOND_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.IRON_SHOVEL),
                new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("hoe_iron_to_diamond"),
                new ItemStack(Material.DIAMOND_HOE),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.IRON_HOE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
        RecipeUtil.addSmithingRecipe(recipe);
        recipe = new SmithingTransformRecipe(
                PurpurExtras.key("sword_iron_to_diamond"),
                new ItemStack(Material.DIAMOND_SWORD),
                new RecipeChoice.MaterialChoice(Material.AIR),
                new RecipeChoice.MaterialChoice(Material.IRON_SWORD),
                new RecipeChoice.MaterialChoice(Material.DIAMOND), true);
        RecipeUtil.addSmithingRecipe(recipe);
    }

    @Override
    public void disable() {
        super.disable();
        RecipeUtil.removeRecipe("sword_iron_to_diamond");
        RecipeUtil.removeRecipe("pick_iron_to_diamond");
        RecipeUtil.removeRecipe("axe_iron_to_diamond");
        RecipeUtil.removeRecipe("shovel_iron_to_diamond");
        RecipeUtil.removeRecipe("hoe_iron_to_diamond");
    }

    @Override
    public String getConfigPath() {
        return "settings.smithing-table.tools.iron-to-diamond";
    }

}
