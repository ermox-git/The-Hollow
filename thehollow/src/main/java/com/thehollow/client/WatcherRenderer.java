package com.thehollow.client;

import com.thehollow.entity.WatcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

/** Reuses the vanilla Enderman model and texture so no custom art is required. */
public class WatcherRenderer extends MobEntityRenderer<WatcherEntity, EndermanEntityModel<WatcherEntity>> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/enderman/enderman.png");

    public WatcherRenderer(EntityRendererFactory.Context context) {
        super(context, new EndermanEntityModel<>(context.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
    }

    @Override
    public Identifier getTexture(WatcherEntity entity) {
        return TEXTURE;
    }
}
