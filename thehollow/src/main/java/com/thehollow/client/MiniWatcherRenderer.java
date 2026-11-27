package com.thehollow.client;

import com.thehollow.TheHollow;
import com.thehollow.entity.MiniWatcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Half-size copy of the Watcher. */
public class MiniWatcherRenderer extends MobEntityRenderer<MiniWatcherEntity, WatcherModel<MiniWatcherEntity>> {
    private static final Identifier TEXTURE = Identifier.of(TheHollow.MOD_ID, "textures/entity/watcher.png");

    public MiniWatcherRenderer(EntityRendererFactory.Context context) {
        super(context, new WatcherModel<>(context.getPart(WatcherModel.LAYER)), 0.25f);
    }

    @Override
    protected void scale(MiniWatcherEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public Identifier getTexture(MiniWatcherEntity entity) {
        return TEXTURE;
    }
}
