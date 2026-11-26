package com.thehollow;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Harmless scares: sounds only, and only when the player is somewhere dark. */
public final class DreadEvents {
    private DreadEvents() {}

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(DreadEvents::tick);
    }

    private static void tick(ServerWorld world) {
        if (world.getTime() % 100 != 0) return;
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) continue;
            BlockPos pos = player.getBlockPos();
            if (world.getLightLevel(pos) > 7) continue;
            if (world.random.nextInt(8) != 0) continue;

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
