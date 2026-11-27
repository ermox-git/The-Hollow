package com.thehollow.client;

import com.thehollow.TheHollow;
import com.thehollow.entity.HollowTitanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class TitanRenderer extends MobEntityRenderer<HollowTitanEntity, TitanModel> {
    private static final Identifier CALM = Identifier.of(TheHollow.MOD_ID, "textures/entity/hollow_titan.png");
    private static final Identifier ENRAGED = Identifier.of(TheHollow.MOD_ID, "textures/entity/hollow_titan_enraged.png");

    public TitanRenderer(EntityRendererFactory.Context context) {
        super(context, new TitanModel(context.getPart(TitanModel.LAYER)), 2.5f);
    }

    @Override
    public Identifier getTexture(HollowTitanEntity entity) {
        return entity.getHealth() < entity.getMaxHealth() * 0.5f ? ENRAGED : CALM;
    }
}
