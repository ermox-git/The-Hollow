package com.thehollow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Right-click: raises a wall in front of you for 50 seconds. Reusable, with a cooldown. */
public class WallRuneItem extends Item {
    public WallRuneItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            if (!Powers.useWall(player)) {
                return TypedActionResult.fail(stack);
            }
            user.getItemCooldownManager().set(this, Powers.WALL_COOLDOWN_TICKS);
        }
        return TypedActionResult.success(stack);
    }
}
