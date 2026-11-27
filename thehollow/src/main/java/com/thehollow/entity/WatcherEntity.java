package com.thehollow.entity;

import com.thehollow.ModItems;
import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The Watcher. Calm: it freezes while a player looks at it (or holds a Ward Lantern nearby)
 * and only the Veilcutter can hurt it. The first real hit makes it roar, then it is enraged
 * for good: no freezing, faster, harder hitting, and it blinks behind you.
 */
public class WatcherEntity extends HostileEntity {
    public static final int CALM = 0;
    public static final int ROARING = 1;
    public static final int ENRAGED = 2;

    private static final TrackedData<Integer> RAGE_STATE =
            DataTracker.registerData(WatcherEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int ROAR_TICKS = 40;

    private boolean observed = false;
    private int roarTicks = 0;

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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RAGE_STATE, CALM);
    }

    public int getRageState() {
        return this.dataTracker.get(RAGE_STATE);
    }

    private void setRageState(int state) {
        this.dataTracker.set(RAGE_STATE, state);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(2, new StalkGoal(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8, 0.02f));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            switch (getRageState()) {
                case ROARING -> {
                    this.observed = false;
                    tickRoar();
                }
                case ENRAGED -> {
                    this.observed = false;
                    tickRage();
                }
                default -> this.observed = isObserved();
            }
        }
        super.tick();
    }

    // ------------------------------------------------------------------ rage

    private void startRoar(PlayerEntity attacker) {
        setRageState(ROARING);
        this.roarTicks = ROAR_TICKS;
        this.setTarget(attacker);
        this.getNavigation().stop();
        this.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 2.0f, 0.7f);
    }

    private void tickRoar() {
        this.roarTicks--;
        this.getNavigation().stop();
        if (this.getWorld() instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 2.0, getZ(), 6, 0.4, 0.6, 0.4, 0.02);
        }
        if (this.roarTicks <= 0) {
            // shockwave at the end of the roar
            for (PlayerEntity p : this.getWorld().getEntitiesByClass(PlayerEntity.class,
                    this.getBoundingBox().expand(8.0), pl -> !pl.isSpectator())) {
                p.takeKnockback(1.2, this.getX() - p.getX(), this.getZ() - p.getZ());
            }
            setRageState(ENRAGED);
            applyRageStats();
        }
    }

    private void applyRageStats() {
        EntityAttributeInstance speed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(0.48);
        EntityAttributeInstance attack = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attack != null) attack.setBaseValue(12.0);
    }

    private void tickRage() {
        LivingEntity target = this.getTarget();
        if (this.age % 20 == 0 && (target == null || !target.isAlive())) {
            PlayerEntity p = this.getWorld().getClosestPlayer(this, 64.0);
            if (p != null && !p.isCreative() && !p.isSpectator()) {
                this.setTarget(p);
            }
        }
        target = this.getTarget();
        if (target != null && this.age % 80 == 0 && this.squaredDistanceTo(target) > 12.0 * 12.0) {
            Vec3d back = target.getRotationVec(1.0f).multiply(-2.5);
            if (this.teleport(target.getX() + back.x, target.getY(), target.getZ() + back.z, true)) {
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.6f);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("WatcherRage", getRageState());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.getInt("WatcherRage") != CALM) {
            setRageState(ENRAGED);
            applyRageStats();
        }
    }

    // ------------------------------------------------------------------ calm rules

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

    /** Frozen while observed, and while roaring. */
    @Override
    protected boolean isImmobile() {
        return this.observed || getRageState() == ROARING || super.isImmobile();
    }

    /** Immune to everything except admin kills and the Veilcutter (and to all damage while roaring). */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        }
        if (getRageState() == ROARING) {
            return false;
        }
        if (source.getAttacker() instanceof PlayerEntity player
                && player.getMainHandStack().isOf(ModItems.VEILCUTTER)) {
            boolean hit = super.damage(source, amount);
            if (hit && !this.getWorld().isClient && getRageState() == CALM) {
                startRoar(player);
            }
            return hit;
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
