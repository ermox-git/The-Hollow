package com.thehollow.entity;

import com.thehollow.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/** A chunk of deepslate hurled by the Titan. */
public class HollowBoulderEntity extends ThrownItemEntity {
    public HollowBoulderEntity(EntityType<? extends ThrownItemEntity> type, World world) {
        super(type, world);
    }

    public HollowBoulderEntity(World world, LivingEntity owner) {
        super(ModEntities.HOLLOW_BOULDER, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.COBBLED_DEEPSLATE;
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        Entity target = hit.getEntity();
        if (target instanceof HollowTitanEntity || target instanceof MiniWatcherEntity) return;
        target.damage(this.getDamageSources().thrown(this, this.getOwner()), 9.0f);
    }

    @Override
    protected void onCollision(HitResult result) {
        super.onCollision(result);
        if (!this.getWorld().isClient) {
            if (this.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 12, 0.3, 0.3, 0.3, 0.05);
            }
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_DEEPSLATE_BREAK,
                    SoundCategory.HOSTILE, 1.0f, 0.8f);
            this.discard();
        }
    }
}
