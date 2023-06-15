package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Predicate;

public class CreeperSquidsModule implements PurpurExtrasModule, Listener {

    @Override
    public void enable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, PurpurExtras.getInstance());
        Entity entity = null;
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.creeper-squid.enabled", false);
    }

    @EventHandler
    public void onSquidSpawn(EntitySpawnEvent ev) {
        if(ev.getEntityType() != EntityType.SQUID) return;
        Squid squid = (Squid) ev.getEntity();
        Bukkit.getMobGoals().removeAllGoals(squid, GoalType.TARGET);
        Bukkit.getMobGoals().addGoal(squid, 3, new SquidGoal(squid));
    }

    //good chunk of this logic pulled directly from nms
    private static final class SquidGoal implements Goal<Squid> {

        private static final GoalKey<Squid> goalKey = GoalKey.of(Squid.class, new NamespacedKey(PurpurExtras.getInstance(), "squidgoal"));
        private static final int maxSwell = 30;
        private static final int maxDistance = 5;
        private static final Predicate<Player> playerPredicate = player -> !player.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                !player.isInvisible() && player.isValid() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE);
        private final Squid squid;
        private int currentSwell = 0;
        private int swellDir = 1;
        private Player currentTarget;

        public SquidGoal(Squid squid) {
            this.squid = squid;
        }

        @Override
        public boolean shouldActivate() {
            if(!squid.isValid()) return false;
            if(currentTarget == null || currentTarget.getLocation().distance(squid.getLocation()) > maxDistance)
                currentTarget = getClosestPlayer();
            if(currentTarget == null) {
                swellDir = 0;
                squid.setGlowing(false);
                return false;
            }else {
                if(swellDir == 0)
                    swellDir = 1;
                return true;
            }
        }


        @Override
        public void stop() {
            squid.setTarget(null);
        }

        @Override
        public void tick() {
            squid.setTarget(currentTarget);
            squid.setGlowing(!squid.isGlowing());
            if(currentTarget != null) {
                swellDir = 1;
            }
            if(swellDir > 0 && currentSwell == 0) {
                squid.getWorld().playSound(squid.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1F, 0.5F);
            }
            this.currentSwell += swellDir;
            if(this.currentSwell >= maxSwell) {
                explode();
            }
        }

        @Override
        public @NotNull GoalKey<Squid> getKey() {
            return goalKey;
        }

        @Override
        public @NotNull EnumSet<GoalType> getTypes() {
            return EnumSet.of(GoalType.TARGET, GoalType.MOVE, GoalType.LOOK);
        }

        private void explode() {
            ExplosionPrimeEvent ev = new ExplosionPrimeEvent(squid, 3, false);
            ev.callEvent();
            if(!ev.isCancelled()) {
                squid.damage(100);
                squid.getWorld().createExplosion(squid, squid.getLocation(), 3);
                squid.getWorld().playSound(squid.getLocation(), Sound.ENTITY_CREEPER_DEATH, 1f, 0.5f);
            } else {
                this.currentSwell = 0;
            }
        }

        private Player getClosestPlayer() {
            Collection<Player> players = squid.getWorld().getNearbyEntitiesByType(Player.class, squid.getLocation(), maxDistance, playerPredicate);
            double playerDistance = -1;
            Player closestPlayer = null;
            for(Player p : players) {
                if(closestPlayer == null) {
                    closestPlayer = p;
                    playerDistance = p.getLocation().distance(squid.getLocation());
                    continue;
                }
                double dist = p.getLocation().distance(squid.getLocation());
                if(dist < playerDistance) {
                    closestPlayer = p;
                    playerDistance = p.getLocation().distance(squid.getLocation());
                }
            }
            return closestPlayer;
        }
    }
}
