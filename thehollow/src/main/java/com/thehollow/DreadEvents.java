package com.thehollow;

import com.thehollow.entity.HollowTitanEntity;
import com.thehollow.entity.WatcherEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Atmosphere. Ambient scares when you are in the dark, plus proximity dread:
 * a heartbeat that speeds up as a Watcher gets closer, darkness pulses, and whispers behind you.
 * Wearing the Hollow Mask makes all of it stronger.
 */
public final class DreadEvents {
    private static final Map<UUID, Long> NEXT_BEAT = new HashMap<>();

    private DreadEvents() {}

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(DreadEvents::tick);
    }

    private static boolean masked(ServerPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.HOLLOW_MASK);
    }

    private static void tick(ServerWorld world) {
        long time = world.getTime();
        if (time % 4 == 0) proximity(world, time);
        if (time % 100 == 0) ambient(world);
    }

    // ------------------------------------------------------------------ proximity dread

    private static void proximity(ServerWorld world, long time) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) continue;
            List<HostileEntity> near = world.getEntitiesByClass(HostileEntity.class,
                    player.getBoundingBox().expand(32.0),
                    e -> e.isAlive() && (e instanceof WatcherEntity || e instanceof HollowTitanEntity));
            if (near.isEmpty()) continue;

            double best = 32.0;
            for (HostileEntity e : near) {
                best = Math.min(best, e.distanceTo(player));
            }
            double closeness = 1.0 - best / 32.0;
            boolean isMasked = masked(player);

            long next = NEXT_BEAT.getOrDefault(player.getUuid(), 0L);
            if (time >= next) {
                float volume = (float) (0.6 + closeness * 0.9 + (isMasked ? 0.3 : 0.0));
                float pitch = (float) (0.8 + closeness * 0.5);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_WARDEN_HEARTBEAT, SoundCategory.AMBIENT, volume, pitch);
                NEXT_BEAT.put(player.getUuid(), time + (long) MathHelper.clamp(10.0 + best * 1.3, 10.0, 52.0));
            }

            if (best < 14.0 && time % 80 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 80, 0, false, false));
            }
            if (best < 10.0 && world.random.nextInt(isMasked ? 30 : 60) == 0) {
                Vec3d behind = player.getRotationVec(1.0f).multiply(-1.0);
                world.playSound(null, player.getX() + behind.x * 3.0, player.getY(), player.getZ() + behind.z * 3.0,
                        SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE, SoundCategory.AMBIENT, 1.0f, 0.7f);
            }
        }
    }

    // ------------------------------------------------------------------ ambient scares

    private static void ambient(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) continue;
            BlockPos pos = player.getBlockPos();
            if (world.getLightLevel(pos) > 7) continue;
            if (world.random.nextInt(masked(player) ? 3 : 8) != 0) continue;

            Vec3d behind = player.getRotationVec(1.0f).multiply(-1.0);
            double x = player.getX() + behind.x * 5.0;
            double y = player.getY();
            double z = player.getZ() + behind.z * 5.0;

            SoundEvent sound;
            float pitch = 0.8f;
            switch (world.random.nextInt(3)) {
                case 0 -> sound = SoundEvents.BLOCK_STONE_STEP;
                case 1 -> {
                    sound = SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;
                    x = player.getX() + (world.random.nextDouble() - 0.5) * 16.0;
                    z = player.getZ() + (world.random.nextDouble() - 0.5) * 16.0;
                }
                default -> {
                    sound = SoundEvents.ENTITY_ENDERMAN_STARE;
                    pitch = 0.5f;
                }
            }
            world.playSound(null, x, y, z, sound, SoundCategory.AMBIENT, 1.0f, pitch);
        }
    }
}
