package com.thehollow;

import com.thehollow.entity.HollowTitanEntity;
import com.thehollow.entity.WatcherEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

/**
 * Reaper's Edge: instantly kills what it hits, then goes on a 100 second cooldown
 * (during the cooldown it is a normal netherite-strength sword).
 * Does not instakill players, and does not work on the Watcher (only the Veilcutter does).
 */
public class ReaperBladeItem extends SwordItem {
    public static final int COOLDOWN_TICKS = 100 * 20;

    public ReaperBladeItem(net.minecraft.item.Item.Settings settings) {
        super(ToolMaterials.NETHERITE, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient
                && attacker instanceof PlayerEntity player
                && !(target instanceof PlayerEntity)
                && !(target instanceof WatcherEntity)
                && !(target instanceof HollowTitanEntity)
                && !player.getItemCooldownManager().isCoolingDown(this)) {
            target.damage(player.getDamageSources().playerAttack(player), Float.MAX_VALUE);
            if (target.isAlive()) {
                target.kill();
            }
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            if (attacker.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1.0, target.getZ(),
                        25, 0.4, 0.8, 0.4, 0.05);
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
