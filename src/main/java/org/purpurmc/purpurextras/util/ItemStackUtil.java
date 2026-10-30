package org.purpurmc.purpurextras.util;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ItemStackUtil {

    /**
     * Damages an item stack
     * @param itemStack the item stack to damage
     * @param amount the amount of damage to deal
     * @param ignoreUnbreaking whether to ignore unbreaking
     * @return whether the item broke
     */
    public static boolean damage(ItemStack itemStack, int amount, boolean ignoreUnbreaking) {
        ItemMeta meta = itemStack.getItemMeta();
        Damageable damageable = (Damageable) itemStack.getItemMeta();
        if (amount > 0) {
            int unbreaking = meta.getEnchantLevel(Enchantment.UNBREAKING);
            int reduce = 0;
            for (int i = 0; unbreaking > 0 && i < amount; ++i) {
                if (reduceDamage(itemStack, ThreadLocalRandom.current(), unbreaking)) {
                    ++reduce;
                }
            }
            amount -= reduce;
            if (amount <= 0) {
                return isBroke(itemStack, damageable.getDamage());
            }
        }
        int damage = damageable.getDamage() + amount;
        damageable.setDamage(damage);
        itemStack.setItemMeta(damageable);
        return isBroke(itemStack, damage);
    }

    public static boolean isBroke(ItemStack itemStack, int damage) {
        if (damage > itemStack.getType().getMaxDurability()) {
            if (itemStack.getAmount() > 0) {
                // ensure it "breaks"
                itemStack.setAmount(0);
            }
            return true;
        }
        return false;
    }

    private static boolean reduceDamage(ItemStack itemStack, Random random, int unbreaking) {
        if (MaterialTags.ARMOR.isTagged(itemStack.getType())) {
            return random.nextFloat() < 0.6F;
        }
        return random.nextInt(unbreaking + 1) > 0;
    }

}
