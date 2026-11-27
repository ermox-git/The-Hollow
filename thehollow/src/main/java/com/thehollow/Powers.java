package com.thehollow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Hollow Relic logic. Choosing a power on the relic shatters it and gives you the matching item.
 * The wall and orb effects themselves live here and are triggered by their items.
 */
public final class Powers {
    public static final int REAPER = 0;
    public static final int WALL = 1;
    public static final int ORB = 2;

    // ---- tweak these ----
    public static final int WALL_DURATION_TICKS = 50 * 20;
    public static final int WALL_COOLDOWN_TICKS = 10 * 20;
    public static final int ORB_RADIUS = 40;
    public static final int ORB_FREEZE_TICKS = 10 * 20;
    public static final int ORB_COOLDOWN_TICKS = 60 * 20;
    // ---------------------

    private record Frozen(RegistryKey<World> dim, UUID id, int expire) {}

    private static final List<Frozen> FROZEN = new ArrayList<>();

    private Powers() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(Powers::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(Powers::shutdown);
    }

    // ------------------------------------------------------------------ relic choice

    public static void activate(ServerPlayerEntity player, int power) {
        ItemStack relic = player.getMainHandStack().isOf(ModItems.HOLLOW_RELIC)
                ? player.getMainHandStack()
                : player.getOffHandStack();
        if (!relic.isOf(ModItems.HOLLOW_RELIC) || power < 0 || power > 2) return;

        Item reward = switch (power) {
            case REAPER -> ModItems.REAPERS_EDGE;
            case WALL -> ModItems.WALL_RUNE;
            default -> ModItems.STILLNESS_ORB;
        };

        relic.decrement(1);
        player.giveItemStack(new ItemStack(reward));

        ServerWorld world = (ServerWorld) player.getWorld();
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 0.5f);
        world.spawnParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1.2, player.getZ(), 30, 0.4, 0.5, 0.4, 0.05);
        player.sendMessage(Text.literal("The relic shatters. Its power takes form."), true);
    }

    // ------------------------------------------------------------------ item effects

    /** Returns false if there was no room for the wall (so the item is not put on cooldown). */
    public static boolean useWall(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;
        ServerWorld world = (ServerWorld) player.getWorld();

        Direction facing = player.getHorizontalFacing();
        Direction side = facing.rotateYClockwise();
        BlockPos center = player.getBlockPos().offset(facing, 3);
        BlockState wallState = ModBlocks.HOLLOW_BARRIER.getDefaultState();
        List<BlockPos> placed = new ArrayList<>();
        for (int sideStep = -3; sideStep <= 3; sideStep++) {
            for (int height = 0; height < 4; height++) {
                BlockPos pos = center.offset(side, sideStep).up(height);
                BlockState current = world.getBlockState(pos);
                if (current.isAir() || current.isReplaceable()) {
                    world.setBlockState(pos, wallState);
                    world.scheduleBlockTick(pos, ModBlocks.HOLLOW_BARRIER, WALL_DURATION_TICKS);
                    world.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            4, 0.3, 0.3, 0.3, 0.02);
                    placed.add(pos);
                }
            }
        }
        if (placed.isEmpty()) {
            player.sendMessage(Text.literal("No room for a wall here."), true);
            return false;
        }
        world.playSound(null, center, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0f, 0.6f);
        return true;
    }

    public static void useOrb(ServerPlayerEntity caster) {
        MinecraftServer server = caster.getServer();
        if (server == null) return;
        ServerWorld world = (ServerWorld) caster.getWorld();
        int now = server.getTicks();

        Box area = new Box(caster.getBlockPos()).expand(ORB_RADIUS);
        double maxSq = (double) ORB_RADIUS * ORB_RADIUS;
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                e -> e != caster && e.isAlive() && e.squaredDistanceTo(caster) <= maxSq);
        for (LivingEntity target : targets) {
            if (target instanceof MobEntity mob) {
                if (!mob.isAiDisabled()) {
                    mob.setAiDisabled(true);
                    FROZEN.add(new Frozen(world.getRegistryKey(), mob.getUuid(), now + ORB_FREEZE_TICKS));
                }
                mob.getNavigation().stop();
            } else {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ORB_FREEZE_TICKS, 9, false, true));
            }
            world.spawnParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY() + target.getHeight() / 2.0,
                    target.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
        }
        world.spawnParticles(ParticleTypes.SNOWFLAKE, caster.getX(), caster.getY() + 1.0, caster.getZ(),
                200, 6.0, 2.0, 6.0, 0.1);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1.5f, 0.5f);
        caster.sendMessage(Text.literal("Everything within " + ORB_RADIUS + " blocks goes still."), true);
    }

    // ------------------------------------------------------------------ timers

    private static void tick(MinecraftServer server) {
        int now = server.getTicks();

        Iterator<Frozen> frozen = FROZEN.iterator();
        while (frozen.hasNext()) {
            Frozen f = frozen.next();
            if (now >= f.expire()) {
                unfreeze(server, f);
                frozen.remove();
            }
        }
    }

    private static void unfreeze(MinecraftServer server, Frozen f) {
        ServerWorld world = server.getWorld(f.dim());
        if (world == null) return;
        Entity entity = world.getEntity(f.id());
        if (entity instanceof MobEntity mob) {
            mob.setAiDisabled(false);
        }
    }

    /** Clean up so nothing stays frozen when the world closes (walls expire on their own scheduled tick). */
    private static void shutdown(MinecraftServer server) {
        for (Frozen f : FROZEN) unfreeze(server, f);
        FROZEN.clear();
    }
}
