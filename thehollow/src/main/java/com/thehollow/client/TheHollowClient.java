package com.thehollow.client;

import com.thehollow.ModEntities;
import com.thehollow.RelicItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

public class TheHollowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(WatcherModel.LAYER, WatcherModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.WATCHER, WatcherRenderer::new);
        RelicItem.openScreen = () -> MinecraftClient.getInstance().setScreen(new PowerSelectScreen());
    }
}
