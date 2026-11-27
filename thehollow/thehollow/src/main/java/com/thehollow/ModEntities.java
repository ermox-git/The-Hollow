package com.thehollow;

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

    private ModEntities() {}

    public static void register() {
        FabricDefaultAttributeRegistry.register(WATCHER, WatcherEntity.createWatcherAttributes());
        SpawnRestriction.register(WATCHER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, WATCHER, 4, 1, 1);
    }
}
