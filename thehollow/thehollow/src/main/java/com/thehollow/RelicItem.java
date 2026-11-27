package com.thehollow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Dropped by the Watcher. Right-click opens the power selection screen (client side). */
public class RelicItem extends Item {
    /** Set by the client entrypoint so this common class never touches client-only code. */
    public static Runnable openScreen = () -> {};

    public RelicItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            openScreen.run();
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
