package com.thehollow.client;

import com.thehollow.ModEntities;
import com.thehollow.RelicItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class TheHollowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(WatcherModel.LAYER, WatcherModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TitanModel.LAYER, TitanModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.WATCHER, WatcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.MINI_WATCHER, MiniWatcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOLLOW_TITAN, TitanRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOLLOW_BOULDER, context -> new FlyingItemEntityRenderer<>(context));

        RelicItem.openScreen = () -> MinecraftClient.getInstance().setScreen(new PowerSelectScreen());
    }
}
