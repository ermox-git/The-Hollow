package com.thehollow;

import com.thehollow.entity.HollowBoulderEntity;
import com.thehollow.entity.HollowTitanEntity;
import com.thehollow.entity.MiniWatcherEntity;
import com.thehollow.entity.WatcherEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public final class ModEntities {
    public static final EntityType<WatcherEntity> WATCHER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(TheHollow.MOD_ID, "watcher"),
            EntityType.Builder.create(WatcherEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 2.9f)
                    .maxTrackingRange(10)
                    .build("watcher"));

    public static final EntityType<MiniWatcherEntity> MINI_WATCHER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(TheHollow.MOD_ID, "mini_watcher"),
            EntityType.Builder.create(MiniWatcherEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.35f, 1.45f)
                    .maxTrackingRange(8)
                    .build("mini_watcher"));

    public static final EntityType<HollowTitanEntity> HOLLOW_TITAN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(TheHollow.MOD_ID, "hollow_titan"),
            EntityType.Builder.create(HollowTitanEntity::new, SpawnGroup.MONSTER)
                    .dimensions(4.8f, 4.6f)
                    .maxTrackingRange(16)
                    .build("hollow_titan"));

    public static final EntityType<HollowBoulderEntity> HOLLOW_BOULDER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(TheHollow.MOD_ID, "hollow_boulder"),
            EntityType.Builder.create(HollowBoulderEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 0.6f)
                    .maxTrackingRange(8)
                    .trackingTickInterval(5)
                    .build("hollow_boulder"));

    private ModEntities() {}

    public static void register() {
        FabricDefaultAttributeRegistry.register(WATCHER, WatcherEntity.createWatcherAttributes());
        FabricDefaultAttributeRegistry.register(MINI_WATCHER, MiniWatcherEntity.createMiniAttributes());
        FabricDefaultAttributeRegistry.register(HOLLOW_TITAN, HollowTitanEntity.createTitanAttributes());
        SpawnRestriction.register(WATCHER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, WATCHER, 4, 1, 1);
    }
}
