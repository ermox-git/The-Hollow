package com.thehollow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Right-click: everything within 40 blocks goes still. Reusable, with a cooldown. */
public class StillnessOrbItem extends Item {
    public StillnessOrbItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            Powers.useOrb(player);
            user.getItemCooldownManager().set(this, Powers.ORB_COOLDOWN_TICKS);
        }
        return TypedActionResult.success(stack);
    }
}
