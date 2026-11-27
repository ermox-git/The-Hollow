package com.thehollow;

import java.util.List;
import java.util.Map;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> HOLLOW_MASK = Registry.registerReference(
            Registries.ARMOR_MATERIAL,
            Identifier.of(TheHollow.MOD_ID, "hollow_mask"),
            new ArmorMaterial(
                    Map.of(ArmorItem.Type.HELMET, 2,
                            ArmorItem.Type.CHESTPLATE, 0,
                            ArmorItem.Type.LEGGINGS, 0,
                            ArmorItem.Type.BOOTS, 0),
                    10,
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.ofItems(ModItems.HOLLOW_SIGIL),
                    List.of(new ArmorMaterial.Layer(Identifier.of(TheHollow.MOD_ID, "hollow_mask"))),
                    0.0f,
                    0.0f));

    private ModArmorMaterials() {}
}
