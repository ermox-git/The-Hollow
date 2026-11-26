package com.thehollow.client;

import com.thehollow.TheHollow;
import com.thehollow.entity.WatcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class WatcherRenderer extends MobEntityRenderer<WatcherEntity, WatcherModel> {
    private static final Identifier TEXTURE = Identifier.of(TheHollow.MOD_ID, "textures/entity/watcher.png");

    public WatcherRenderer(EntityRendererFactory.Context context) {
        super(context, new WatcherModel(context.getPart(WatcherModel.LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(WatcherEntity entity) {
        return TEXTURE;
    }
}
