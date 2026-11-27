package com.thehollow;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
    public static final Item HOLLOW_SIGIL = add("hollow_sigil",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item VEILCUTTER = add("veilcutter",
            new SwordItem(ToolMaterials.NETHERITE, new Item.Settings()
                    .rarity(Rarity.EPIC)
                    .fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 4, -2.2f))));

    public static final Item WARD_LANTERN = add("ward_lantern",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item WATCHER_SPAWN_EGG = add("watcher_spawn_egg",
            new SpawnEggItem(ModEntities.WATCHER, 0xE8E4D8, 0x1A0000, new Item.Settings()));

    public static final Item HOLLOW_RELIC = add("hollow_relic",
            new RelicItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item REAPERS_EDGE = add("reapers_edge",
            new ReaperBladeItem(new Item.Settings()
                    .rarity(Rarity.EPIC)
                    .fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 4, -2.2f))));

    public static final Item WALL_RUNE = add("wall_rune",
            new WallRuneItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item STILLNESS_ORB = add("stillness_orb",
            new StillnessOrbItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item HOLLOW_MASK = add("hollow_mask",
            new ArmorItem(ModArmorMaterials.HOLLOW_MASK, ArmorItem.Type.HELMET,
                    new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item HOLLOW_ALTAR = add("hollow_altar",
            new BlockItem(ModBlocks.HOLLOW_ALTAR, new Item.Settings().rarity(Rarity.EPIC)));

    private ModItems() {}

    private static Item add(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(TheHollow.MOD_ID, id), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(e -> {
            e.add(HOLLOW_SIGIL);
            e.add(HOLLOW_RELIC);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(e -> {
            e.add(VEILCUTTER);
            e.add(REAPERS_EDGE);
            e.add(HOLLOW_MASK);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(e -> e.add(HOLLOW_ALTAR));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(e -> {
            e.add(WARD_LANTERN);
            e.add(WALL_RUNE);
            e.add(STILLNESS_ORB);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(e -> e.add(WATCHER_SPAWN_EGG));
    }
}
