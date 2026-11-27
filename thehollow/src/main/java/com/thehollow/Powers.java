package com.thehollow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
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

/** Server-side logic for the three Hollow Relic powers. All timings are in one place. */
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

    private static final String[] NAMES = {"Reaper's Edge", "Hollow Wall", "Stillness Orb"};

    private record Wall(RegistryKey<World> dim, List<BlockPos> blocks, int expire) {}
    private record Frozen(RegistryKey<World> dim, UUID id, int expire) {}

    private static final Map<UUID, int[]> READY = new HashMap<>();
    private static final List<Wall> WALLS = new ArrayList<>();
    private static final List<Frozen> FROZEN = new ArrayList<>();

    private Powers() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(Powers::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(Powers::shutdown);
    }

    // ------------------------------------------------------------------ activation

    public static void activate(ServerPlayerEntity player, int power) {
        boolean holdsRelic = player.getMainHandStack().isOf(ModItems.HOLLOW_RELIC)
                || player.getOffHandStack().isOf(ModItems.HOLLOW_RELIC);
        if (!holdsRelic || power < 0 || power > 2) return;
        MinecraftServer server = player.getServer();
        if (server == null) return;
        ServerWorld world = (ServerWorld) player.getWorld();
        int now = server.getTicks();

        switch (power) {
            case REAPER -> giveReaper(player);
            case WALL -> {
                if (onCooldown(player, WALL, now)) return;
                if (buildWall(player, world, now)) {
                    setCooldown(player, WALL, WALL_COOLDOWN_TICKS, now);
                }
            }
            default -> {
                if (onCooldown(player, ORB, now)) return;
                freezeEverything(player, world, now);
                setCooldown(player, ORB, ORB_COOLDOWN_TICKS, now);
            }
        }
    }

    private static void giveReaper(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isOf(ModItems.REAPERS_EDGE)) {
                player.sendMessage(Text.literal("You already carry the Reaper's Edge."), true);
                return;
            }
        }
        player.giveItemStack(new ItemStack(ModItems.REAPERS_EDGE));
        player.sendMessage(Text.literal("The Reaper's Edge answers your call."), true);
    }

    private static boolean buildWall(ServerPlayerEntity player, ServerWorld world, int now) {
        Direction facing = player.getHorizontalFacing();
        Direction side = facing.rotateYClockwise();
        BlockPos center = player.getBlockPos().offset(facing, 3);
        BlockState wallState = Blocks.BLACKSTONE.getDefaultState();
        List<BlockPos> placed = new ArrayList<>();
        for (int sideStep = -3; sideStep <= 3; sideStep++) {
            for (int height = 0; height < 4; height++) {
                BlockPos pos = center.offset(side, sideStep).up(height);
                BlockState current = world.getBlockState(pos);
                if (current.isAir() || current.isReplaceable()) {
                    world.setBlockState(pos, wallState);
                    placed.add(pos);
                }
            }
        }
        if (placed.isEmpty()) {
            player.sendMessage(Text.literal("No room for a wall here."), true);
            return false;
        }
        WALLS.add(new Wall(world.getRegistryKey(), placed, now + WALL_DURATION_TICKS));
        world.playSound(null, center, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0f, 0.6f);
        return true;
    }

    private static void freezeEverything(ServerPlayerEntity caster, ServerWorld world, int now) {
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

    // ------------------------------------------------------------------ cooldowns

    private static boolean onCooldown(ServerPlayerEntity player, int slot, int now) {
        int[] ready = READY.computeIfAbsent(player.getUuid(), k -> new int[3]);
        if (now < ready[slot]) {
            int seconds = (ready[slot] - now + 19) / 20;
            player.sendMessage(Text.literal(NAMES[slot] + " recharging: " + seconds + "s"), true);
            return true;
        }
        return false;
    }

    private static void setCooldown(ServerPlayerEntity player, int slot, int ticks, int now) {
        READY.computeIfAbsent(player.getUuid(), k -> new int[3])[slot] = now + ticks;
    }

    // ------------------------------------------------------------------ timers

    private static void tick(MinecraftServer server) {
        int now = server.getTicks();

        Iterator<Wall> walls = WALLS.iterator();
        while (walls.hasNext()) {
            Wall wall = walls.next();
            if (now >= wall.expire()) {
                removeWall(server, wall);
                walls.remove();
            }
        }

        Iterator<Frozen> frozen = FROZEN.iterator();
        while (frozen.hasNext()) {
            Frozen f = frozen.next();
            if (now >= f.expire()) {
                unfreeze(server, f);
                frozen.remove();
            }
        }
    }

    private static void removeWall(MinecraftServer server, Wall wall) {
        ServerWorld world = server.getWorld(wall.dim());
        if (world == null) return;
        for (BlockPos pos : wall.blocks()) {
            if (world.getBlockState(pos).isOf(Blocks.BLACKSTONE)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
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

    /** Clean up so nothing stays frozen or walled when the world closes. */
    private static void shutdown(MinecraftServer server) {
        for (Wall wall : WALLS) removeWall(server, wall);
        for (Frozen f : FROZEN) unfreeze(server, f);
        WALLS.clear();
        FROZEN.clear();
        READY.clear();
    }
}
