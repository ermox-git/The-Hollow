package com.thehollow.entity;

import com.thehollow.ModItems;
import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The Watcher. It freezes while a player looks at it (or holds a Ward Lantern
 * nearby), and only the Veilcutter can hurt it.
 */
public class WatcherEntity extends HostileEntity {
    private boolean observed = false;

    public WatcherEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createWatcherAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, false));
        // Walks toward the nearest player even when it cannot target them (e.g. creative mode).
        this.goalSelector.add(2, new StalkGoal(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8, 0.02f));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            this.observed = isObserved();
        }
        super.tick();
    }

    private boolean isObserved() {
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (player.isSpectator()) continue;
            double distSq = player.squaredDistanceTo(this);
            if (distSq > 40.0 * 40.0) continue;

            boolean holdsLantern = player.getMainHandStack().isOf(ModItems.WARD_LANTERN)
                    || player.getOffHandStack().isOf(ModItems.WARD_LANTERN);
            if (holdsLantern && distSq < 10.0 * 10.0) return true;

            Vec3d look = player.getRotationVec(1.0f);
            Vec3d toMe = new Vec3d(
                    this.getX() - player.getX(),
                    this.getEyeY() - player.getEyeY(),
                    this.getZ() - player.getZ()).normalize();
            if (look.dotProduct(toMe) > 0.75 && player.canSee(this)) return true;
        }
        return false;
    }

    /** While observed the Watcher cannot move, path, or attack. */
    @Override
    protected boolean isImmobile() {
        return this.observed || super.isImmobile();
    }

    /** Immune to everything except admin kills and the Veilcutter. */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        }
        if (source.getAttacker() instanceof PlayerEntity player
                && player.getMainHandStack().isOf(ModItems.VEILCUTTER)) {
            return super.damage(source, amount);
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    /** Slowly closes in on the nearest player. */
    private static class StalkGoal extends Goal {
        private final WatcherEntity watcher;
        private PlayerEntity prey;

        StalkGoal(WatcherEntity watcher) {
            this.watcher = watcher;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.prey = this.watcher.getWorld().getClosestPlayer(this.watcher, 40.0);
            return this.prey != null && !this.prey.isSpectator();
        }

        @Override
        public boolean shouldContinue() {
            return this.prey != null && this.prey.isAlive() && !this.prey.isSpectator()
                    && this.watcher.squaredDistanceTo(this.prey) < 45.0 * 45.0;
        }

        @Override
        public void tick() {
            if (this.watcher.age % 10 == 0) {
                this.watcher.getNavigation().startMovingTo(this.prey, 1.0);
            }
        }

        @Override
        public void stop() {
            this.watcher.getNavigation().stop();
            this.prey = null;
        }
    }
}
