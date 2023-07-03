package org.purpurmc.purpurextras.modules.implementation;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Allows squids to act as Creepers - they will explode if within {@link #maxDistance} after a fuse of {@link #maxSwell}
 * ticks, as well as moving towards you at a velocity of {@link #velocity}
 * Explosion power / radius is also configurable through {@link #explosionPower}
 */
@ModuleInfo(name = "Creeper Squids", description = "Squids will explode like Creepers!")
public class CreeperSquidsModule extends PurpurExtrasModule implements Listener {

    private int maxSwell;
    private int maxDistance;
    private int explosionPower;
    private double velocity;

    public CreeperSquidsModule(PurpurConfig config) {
        super(config);
    }

    @Override
    public void enable() {
        super.enable();
        maxSwell = getConfigInt("settings.creeper-squid.fuse-ticks", 30);
        maxDistance = (int) Math.pow(getConfigInt("settings.creeper-squid.agro-distance", 7), 2);
        explosionPower = getConfigInt("settings.creeper-squid.explosion-radius", 3);
        velocity = getConfigDouble("settings.creeper-squid.velocity", 0.5);
    }

    @Override
    public String getConfigPath() {
        return "settings.creeper-squid";
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onSquidSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.SQUID) return;
        Squid squid = (Squid) event.getEntity();
        Bukkit.getMobGoals().removeAllGoals(squid, GoalType.TARGET);
        Bukkit.getMobGoals().addGoal(squid, 3, new SquidGoal(squid));
    }

    //good chunk of this logic pulled directly from nms
    private final class SquidGoal implements Goal<Squid> {

        private static final GoalKey<Squid> goalKey = GoalKey.of(Squid.class, PurpurExtras.key("squidgoal"));
        private static final Predicate<Player> playerPredicate = player -> !player.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                !player.isInvisible() && player.isValid() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE);
        private final Squid squid;
        private int currentSwell = 0;
        private int swellDir = -1;
        private Player currentTarget;

        public SquidGoal(Squid squid) {
            this.squid = squid;
        }

        @Override
        public boolean shouldActivate() {
            if (!squid.isValid()) return false;
            if(currentTarget != null) {
                if(currentTarget.getLocation().distanceSquared(squid.getLocation()) <= maxDistance) {
                    return true;
                }
            }
            currentTarget = getClosestPlayer();
            if(currentTarget == null) {
                swellDir = -1;
                if (currentSwell > 0) currentSwell += swellDir;
                squid.setGlowing(false);
                return false;
            }
            return true;
        }


        @Override
        public void stop() {
            squid.setTarget(null);
            squid.setVelocity(Vector.getRandom());
        }

        @Override
        public void tick() {
            squid.setGlowing(!squid.isGlowing());
            if (currentTarget != null) {
                if (swellDir == -1)
                    squid.getWorld().playSound(squid.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1F, 0.5F);
                swellDir = 1;
                squid.setVelocity(squid.getLocation().toVector().subtract(currentTarget.getLocation().toVector()).normalize().multiply(-1 * velocity));
                squid.lookAt(currentTarget.getEyeLocation());
            }
            this.currentSwell += swellDir;
            if (this.currentSwell >= maxSwell) {
                explode();
            }
        }

        @Override
        public @NotNull GoalKey<Squid> getKey() {
            return goalKey;
        }

        @Override
        public @NotNull EnumSet<GoalType> getTypes() {
            return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
        }

        private void explode() {
            ExplosionPrimeEvent event = new ExplosionPrimeEvent(squid, 3, true);
            event.callEvent();
            if (!event.isCancelled()) {
                squid.remove();
                squid.getWorld().createExplosion(squid, squid.getLocation(), explosionPower);
                Bukkit.getScheduler().runTaskLater(PurpurExtras.getInstance(),
                        () -> squid.getWorld().playSound(squid.getLocation(), Sound.ENTITY_CREEPER_DEATH, 1f, 0.5f), 1);
            } else {
                this.currentSwell = 0;
            }
        }

        private Player getClosestPlayer() {
            Collection<Player> players = squid.getWorld().getNearbyEntitiesByType(Player.class, squid.getLocation(), maxDistance, playerPredicate);
            double playerDistance = -1;
            Player closestPlayer = null;
            for (Player player : players) {
                if (closestPlayer == null) {
                    closestPlayer = player;
                    playerDistance = player.getLocation().distanceSquared(squid.getLocation());
                    continue;
                }
                double dist = player.getLocation().distanceSquared(squid.getLocation());
                if (dist < playerDistance) {
                    closestPlayer = player;
                    playerDistance = player.getLocation().distanceSquared(squid.getLocation());
                }
            }
            return closestPlayer;
        }
    }
}
