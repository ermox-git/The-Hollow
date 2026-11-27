package com.thehollow.entity;

import com.thehollow.ModEntities;
import com.thehollow.ModItems;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * The Hollow Titan: only its upper half rises from the altar. It cannot move, but it hurls
 * boulders, slams anything that gets close, and keeps summoning mini Watchers.
 * Only the Veilcutter can hurt it.
 */
public class HollowTitanEntity extends HostileEntity {
    public static final int RISE_TICKS = 80;

    private static final TrackedData<Integer> ANIM =
            DataTracker.registerData(HollowTitanEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final ServerBossBar bossBar =
            new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);

    private int throwCooldown = 60;
    private int summonCooldown = 120;
    private int slamCooldown = 0;

    public HollowTitanEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 80;
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createTitanAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANIM, 0);
    }

    /** Ticks left of the arms-up attack pose (0 = idle). */
    public int getAnim() {
        return this.dataTracker.get(ANIM);
    }

    private boolean enragedPhase() {
        return this.getHealth() < this.getMaxHealth() * 0.5f;
    }

    // ------------------------------------------------------------------ behaviour

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;

        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        int anim = getAnim();
        if (anim > 0) this.dataTracker.set(ANIM, anim - 1);

        if (this.age < RISE_TICKS) {
            if (this.age == 1) {
                this.playSound(SoundEvents.ENTITY_WARDEN_EMERGE, 3.0f, 0.6f);
            }
            if (this.age % 4 == 0 && this.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.POOF, getX(), getY() + 0.2, getZ(), 8, 1.2, 0.1, 1.2, 0.02);
            }
            return;
        }
        if (this.isAiDisabled()) return; // frozen by a Stillness Orb

        PlayerEntity target = this.getWorld().getClosestPlayer(getX(), getY(), getZ(), 48.0,
                e -> e instanceof PlayerEntity p && !p.isCreative() && !p.isSpectator());
        if (target == null) return;

        faceTarget(target);
        this.throwCooldown--;
        this.summonCooldown--;
        this.slamCooldown--;

        if (this.slamCooldown <= 0 && this.squaredDistanceTo(target) < 7.0 * 7.0) {
            slam();
            this.slamCooldown = 40;
        }
        if (this.throwCooldown <= 0) {
            throwBoulder(target, 0.0f);
            if (enragedPhase()) throwBoulder(target, 6.0f);
            this.throwCooldown = enragedPhase() ? 30 : 50;
        }
        if (this.summonCooldown <= 0) {
            summonMinions();
            this.summonCooldown = enragedPhase() ? 140 : 200;
        }
    }

    private void faceTarget(PlayerEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        this.setYaw(yaw);
        this.setBodyYaw(yaw);
        this.setHeadYaw(yaw);
    }

    private void slam() {
        this.dataTracker.set(ANIM, 12);
        this.playSound(SoundEvents.ENTITY_WARDEN_ATTACK_IMPACT, 3.0f, 0.7f);
        for (PlayerEntity p : this.getWorld().getEntitiesByClass(PlayerEntity.class,
                this.getBoundingBox().expand(7.0), pl -> !pl.isSpectator() && !pl.isCreative())) {
            p.damage(this.getDamageSources().mobAttack(this), 10.0f);
            p.takeKnockback(1.6, this.getX() - p.getX(), this.getZ() - p.getZ());
        }
    }

    private void throwBoulder(PlayerEntity target, float spread) {
        this.dataTracker.set(ANIM, 12);
        this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 2.0f, 0.8f);
        HollowBoulderEntity boulder = new HollowBoulderEntity(this.getWorld(), this);
        boulder.setPosition(this.getX(), this.getY() + 4.5, this.getZ());
        double dx = target.getX() - boulder.getX();
        double dy = target.getBodyY(0.33) - boulder.getY();
        double dz = target.getZ() - boulder.getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        boulder.setVelocity(dx, dy + flat * 0.2, dz, 1.4f, 2.0f + spread);
        this.getWorld().spawnEntity(boulder);
    }

    private void summonMinions() {
        List<MiniWatcherEntity> alive = this.getWorld().getEntitiesByClass(MiniWatcherEntity.class,
                this.getBoundingBox().expand(24.0), Entity::isAlive);
        if (alive.size() >= 6) return;
        for (int i = 0; i < 2; i++) {
            for (int attempt = 0; attempt < 6; attempt++) {
                double angle = this.random.nextDouble() * Math.PI * 2.0;
                double radius = 4.0 + this.random.nextDouble() * 2.5;
                BlockPos pos = BlockPos.ofFloored(getX() + Math.cos(angle) * radius, getY(), getZ() + Math.sin(angle) * radius);
                if (this.getWorld().getBlockState(pos).isAir() && this.getWorld().getBlockState(pos.up()).isAir()) {
                    MiniWatcherEntity mini = ModEntities.MINI_WATCHER.create(this.getWorld());
                    if (mini != null) {
                        mini.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                                this.random.nextFloat() * 360.0f, 0.0f);
                        this.getWorld().spawnEntity(mini);
                    }
                    break;
                }
            }
        }
        this.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 2.0f, 1.0f);
    }

    // ------------------------------------------------------------------ rules

    /** Rooted to the altar. */
    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        }
        if (this.age < RISE_TICKS) return false;
        if (source.getAttacker() instanceof PlayerEntity player
                && player.getMainHandStack().isOf(ModItems.VEILCUTTER)) {
            return super.damage(source, amount);
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            for (MiniWatcherEntity mini : this.getWorld().getEntitiesByClass(MiniWatcherEntity.class,
                    this.getBoundingBox().expand(64.0), Entity::isAlive)) {
                mini.kill();
            }
        }
    }

    // ------------------------------------------------------------------ boss bar

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }
}
