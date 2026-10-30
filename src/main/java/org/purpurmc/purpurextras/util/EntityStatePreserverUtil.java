package org.purpurmc.purpurextras.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;


public class EntityStatePreserverUtil {

    private EntityStatePreserverUtil() {

    }

    /**
     * Copy persistent state from one LivingEntity to another.
     */
    public static void preserveEntityState(LivingEntity oldEntity, LivingEntity transformedEntity) {
        if (oldEntity == null || transformedEntity == null) return;
        preserveGeneralProperties(oldEntity, transformedEntity);

    }


    private static void preserveGeneralProperties(LivingEntity oldEntity, LivingEntity transformedEntity) {
        preserveEquipment(oldEntity.getEquipment(), transformedEntity);
        preserveHealth(oldEntity, transformedEntity);
        preserveSurvivalState(oldEntity, transformedEntity);
        preservePotionEffect(oldEntity, transformedEntity);
        preserveFlags(oldEntity, transformedEntity);
        preserveFireAndFreeze(oldEntity, transformedEntity);
        preservePoseAndRotation(oldEntity, transformedEntity);
        preserveExtraStates(oldEntity, transformedEntity);


    }

    private static void preserveExtraStates(LivingEntity oldEntity, LivingEntity transformedEntity) {
        preserveVelocity(oldEntity, transformedEntity);
        transformedEntity.setPortalCooldown(oldEntity.getPortalCooldown());
        transformedEntity.setAI(oldEntity.hasAI());
        transformedEntity.setGliding(oldEntity.isGliding());
        transformedEntity.setSneaking(oldEntity.isSneaking());
    }

    private static void preserveSurvivalState(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.setAbsorptionAmount(oldEntity.getAbsorptionAmount());
        transformedEntity.setMaximumAir(oldEntity.getMaximumAir());
        transformedEntity.setRemainingAir(oldEntity.getRemainingAir());
        transformedEntity.setNoDamageTicks(oldEntity.getNoDamageTicks());
    }

    private static void preserveVelocity(LivingEntity oldEntity, LivingEntity transformedEntity) {
        // Velocity (only if same entity type family, else may cause issues)
        if (oldEntity.getType() == transformedEntity.getType()) {
            transformedEntity.setVelocity(oldEntity.getVelocity());
        }
    }

    private static void preservePoseAndRotation(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.setPose(oldEntity.getPose());
        transformedEntity.setRotation(oldEntity.getLocation().getYaw(), oldEntity.getLocation().getPitch());
    }

    private static void preserveFireAndFreeze(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.setFireTicks(oldEntity.getFireTicks());
        transformedEntity.setFreezeTicks(oldEntity.getFreezeTicks());
    }

    private static void preserveFlags(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
        transformedEntity.setGlowing(oldEntity.isGlowing());
        transformedEntity.setGravity(oldEntity.hasGravity());
        transformedEntity.setCollidable(oldEntity.isCollidable());
        transformedEntity.setSilent(oldEntity.isSilent());
        transformedEntity.setInvisible(oldEntity.isInvisible());
        transformedEntity.setInvulnerable(oldEntity.isInvulnerable());
        transformedEntity.setPersistent(oldEntity.isPersistent());
    }

    private static void preservePotionEffect(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.getActivePotionEffects().forEach(pe -> transformedEntity.removePotionEffect(pe.getType()));
        for (PotionEffect effect : oldEntity.getActivePotionEffects()) {
            transformedEntity.addPotionEffect(effect);
        }
    }

    private static void preserveHealth(LivingEntity oldEntity, LivingEntity transformedEntity) {
        transformedEntity.setHealth(oldEntity.getHealth());
    }

    private static void preserveEquipment(EntityEquipment oldEquipment, Entity spawnedEntity) {
        if (spawnedEntity instanceof LivingEntity newEntity && oldEquipment != null) {
            EntityEquipment newEquipment = newEntity.getEquipment();
            if (newEquipment != null) {
                // Copy armor items
                newEquipment.setArmorContents(oldEquipment.getArmorContents());

                // Copy armor drop chances
                newEquipment.setHelmetDropChance(oldEquipment.getHelmetDropChance());
                newEquipment.setChestplateDropChance(oldEquipment.getChestplateDropChance());
                newEquipment.setLeggingsDropChance(oldEquipment.getLeggingsDropChance());
                newEquipment.setBootsDropChance(oldEquipment.getBootsDropChance());

                // Copy hand items and their drop chances
                newEquipment.setItemInMainHand(oldEquipment.getItemInMainHand());
                newEquipment.setItemInMainHandDropChance(oldEquipment.getItemInMainHandDropChance());
                newEquipment.setItemInOffHand(oldEquipment.getItemInOffHand());
                newEquipment.setItemInOffHandDropChance(oldEquipment.getItemInOffHandDropChance());
            }
        }
    }


}
