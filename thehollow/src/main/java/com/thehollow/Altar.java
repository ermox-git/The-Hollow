package com.thehollow;

import com.thehollow.entity.HollowTitanEntity;
import java.util.List;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/** Right-click a Hollow Altar while holding a Hollow Relic to sacrifice it and wake the Titan. */
public final class Altar {
    private Altar() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            if (!world.getBlockState(pos).isOf(ModBlocks.HOLLOW_ALTAR)) return ActionResult.PASS;
            ItemStack held = player.getStackInHand(hand);
            if (!held.isOf(ModItems.HOLLOW_RELIC)) return ActionResult.PASS;
            if (world.isClient) return ActionResult.SUCCESS;
            summon((ServerWorld) world, pos, player, held);
            return ActionResult.SUCCESS;
        });
    }

    private static void summon(ServerWorld world, BlockPos altar, PlayerEntity player, ItemStack relic) {
        List<HollowTitanEntity> existing = world.getEntitiesByClass(HollowTitanEntity.class,
                new Box(altar).expand(64.0), HollowTitanEntity::isAlive);
        if (!existing.isEmpty()) {
            player.sendMessage(Text.literal("A Titan already stands."), true);
            return;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos check = altar.add(dx, dy, dz);
                    var state = world.getBlockState(check);
                    if (!(state.isAir() || state.isReplaceable())) {
                        player.sendMessage(Text.literal("Not enough room above the altar."), true);
                        return;
                    }
                }
            }
        }

        HollowTitanEntity titan = ModEntities.HOLLOW_TITAN.create(world);
        if (titan == null) return;

        relic.decrement(1);
        titan.refreshPositionAndAngles(altar.getX() + 0.5, altar.getY() + 1.0, altar.getZ() + 0.5,
                player.getYaw() + 180.0f, 0.0f);
        world.spawnEntity(titan);

        world.playSound(null, altar, SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.HOSTILE, 3.0f, 0.6f);
        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, altar.getX() + 0.5, altar.getY() + 1.2, altar.getZ() + 0.5,
                60, 1.0, 0.6, 1.0, 0.05);
        player.sendMessage(Text.literal("The relic crumbles. Something answers from below."), true);
    }
}
