package com.thehollow;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block HOLLOW_ALTAR = Registry.register(
            Registries.BLOCK,
            Identifier.of(TheHollow.MOD_ID, "hollow_altar"),
            new Block(AbstractBlock.Settings.create()
                    .strength(5.0f, 1200.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)
                    .luminance(state -> 5)));

    /** Placed by the Wall Rune. Not obtainable as an item. */
    public static final Block HOLLOW_BARRIER = Registry.register(
            Registries.BLOCK,
            Identifier.of(TheHollow.MOD_ID, "hollow_barrier"),
            new HollowBarrierBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.CLEAR)
                    .strength(-1.0f, 3600000.0f)
                    .dropsNothing()
                    .nonOpaque()
                    .noBlockBreakParticles()));

    private ModBlocks() {}

    /** Touching this class registers the blocks. */
    public static void register() {}
}
