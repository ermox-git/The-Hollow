package com.thehollow.client;

import com.thehollow.TheHollow;
import com.thehollow.entity.HollowTitanEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/** The Giant: custom Blockbench model (converted from Mojang mappings to Yarn). */
public class TitanModel extends SinglePartEntityModel<HollowTitanEntity> {
    public static final EntityModelLayer LAYER =
            new EntityModelLayer(Identifier.of(TheHollow.MOD_ID, "hollow_titan"), "main");

    private final ModelPart root;
    private final ModelPart bbMain;

    public TitanModel(ModelPart root) {
        this.root = root;
        this.bbMain = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData bb_main = root.addChild("bb_main", ModelPartBuilder.create().uv(0, 53).cuboid(-11.0F, -42.0F, -14.0F, 22.0F, 42.0F, 28.0F).uv(0, 381).cuboid(-26.0F, -1.0F, -25.0F, 52.0F, 1.0F, 52.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(204, 53).cuboid(7.0F, -24.0F, -11.0F, 22.0F, 6.0F, 8.0F),
                ModelTransform.of(0.0F, 21.0F, -87.0F, 0.0F, -1.5272F, 0.0F));

        ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(0, 123).cuboid(7.0F, -24.0F, -11.0F, 29.0F, 6.0F, 8.0F),
                ModelTransform.of(0.0F, 21.0F, -39.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData cube_r3 = bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(0, 123).cuboid(7.0F, -24.0F, -11.0F, 29.0F, 6.0F, 8.0F),
                ModelTransform.of(2.0F, 21.0F, -40.0F, 0.0F, 0.6109F, 0.0F));

        ModelPartData cube_r4 = bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(204, 67).cuboid(7.0F, -24.0F, -11.0F, 17.0F, 6.0F, 8.0F),
                ModelTransform.of(-13.0F, 21.0F, -85.0F, 0.0F, -1.5272F, 0.0F));

        ModelPartData cube_r5 = bb_main.addChild("cube_r5", ModelPartBuilder.create().uv(204, 67).cuboid(7.0F, -24.0F, -11.0F, 17.0F, 6.0F, 8.0F),
                ModelTransform.of(-13.0F, 21.0F, 44.0F, 0.0F, -1.5272F, 0.0F));

        ModelPartData cube_r6 = bb_main.addChild("cube_r6", ModelPartBuilder.create().uv(204, 53).cuboid(7.0F, -24.0F, -11.0F, 22.0F, 6.0F, 8.0F),
                ModelTransform.of(0.0F, 21.0F, 46.0F, 0.0F, -1.5272F, 0.0F));

        ModelPartData cube_r7 = bb_main.addChild("cube_r7", ModelPartBuilder.create().uv(0, 123).cuboid(7.0F, -24.0F, -11.0F, 29.0F, 6.0F, 8.0F),
                ModelTransform.of(0.0F, 21.0F, 46.0F, 0.0F, -0.6981F, 0.0F));

        ModelPartData cube_r8 = bb_main.addChild("cube_r8", ModelPartBuilder.create().uv(0, 123).cuboid(7.0F, -24.0F, -11.0F, 29.0F, 6.0F, 8.0F),
                ModelTransform.of(0.0F, 21.0F, 46.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData cube_r9 = bb_main.addChild("cube_r9", ModelPartBuilder.create().uv(200, 97).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -36.0F, 41.0F, -3.0107F, 0.0F, 0.0F));

        ModelPartData cube_r10 = bb_main.addChild("cube_r10", ModelPartBuilder.create().uv(40, 188).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -37.0F, -47.0F, 3.0543F, 0.0F, 0.0F));

        ModelPartData cube_r11 = bb_main.addChild("cube_r11", ModelPartBuilder.create().uv(32, 137).cuboid(10.0F, -24.5F, -14.0F, 1.0F, 3.0F, 3.0F).uv(24, 137).cuboid(10.0F, -24.5F, -38.0F, 1.0F, 3.0F, 3.0F).uv(16, 137).cuboid(10.0F, -24.5F, -34.0F, 1.0F, 3.0F, 3.0F).uv(8, 137).cuboid(10.0F, -24.5F, -30.0F, 1.0F, 3.0F, 3.0F).uv(0, 137).cuboid(10.0F, -24.5F, -26.0F, 1.0F, 3.0F, 3.0F).uv(90, 133).cuboid(10.0F, -24.5F, -22.0F, 1.0F, 3.0F, 3.0F).uv(82, 133).cuboid(10.0F, -24.5F, -18.0F, 1.0F, 3.0F, 3.0F),
                ModelTransform.of(7.0F, -26.0F, 24.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r12 = bb_main.addChild("cube_r12", ModelPartBuilder.create().uv(74, 133).cuboid(10.0F, -24.5F, -14.0F, 1.0F, 3.0F, 3.0F).uv(90, 128).cuboid(10.0F, -24.5F, -18.0F, 1.0F, 3.0F, 3.0F).uv(82, 128).cuboid(10.0F, -24.5F, -22.0F, 1.0F, 3.0F, 3.0F).uv(74, 128).cuboid(10.0F, -24.5F, -26.0F, 1.0F, 3.0F, 3.0F).uv(90, 123).cuboid(10.0F, -24.5F, -30.0F, 1.0F, 3.0F, 3.0F).uv(82, 123).cuboid(10.0F, -24.5F, -34.0F, 1.0F, 3.0F, 3.0F).uv(74, 123).cuboid(10.0F, -24.5F, -38.0F, 1.0F, 3.0F, 3.0F).uv(104, 145).cuboid(-11.0F, -22.5F, -38.0F, 22.0F, 8.0F, 28.0F),
                ModelTransform.of(3.0F, -26.0F, 24.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData cube_r13 = bb_main.addChild("cube_r13", ModelPartBuilder.create().uv(100, 97).cuboid(-11.0F, -35.5F, -14.0F, 22.0F, 21.0F, 28.0F),
                ModelTransform.of(12.0F, -32.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

        ModelPartData cube_r14 = bb_main.addChild("cube_r14", ModelPartBuilder.create().uv(0, 188).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -55.0F, 25.0F, -2.4435F, 0.0F, 0.0F));

        ModelPartData cube_r15 = bb_main.addChild("cube_r15", ModelPartBuilder.create().uv(184, 180).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -55.0F, 27.0F, 2.4435F, 0.0F, 0.0F));

        ModelPartData cube_r16 = bb_main.addChild("cube_r16", ModelPartBuilder.create().uv(100, 53).cuboid(-13.0F, -40.0F, -11.0F, 30.0F, 22.0F, 22.0F),
                ModelTransform.of(0.0F, 21.0F, 43.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData cube_r17 = bb_main.addChild("cube_r17", ModelPartBuilder.create().uv(0, 145).cuboid(-13.0F, -40.0F, -11.0F, 30.0F, 21.0F, 22.0F),
                ModelTransform.of(0.0F, -38.0F, -47.0F, 3.0543F, 0.0F, 0.0F));

        ModelPartData cube_r18 = bb_main.addChild("cube_r18", ModelPartBuilder.create().uv(144, 180).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -55.0F, -31.0F, 2.4435F, 0.0F, 0.0F));

        ModelPartData cube_r19 = bb_main.addChild("cube_r19", ModelPartBuilder.create().uv(104, 180).cuboid(-3.0F, -28.0F, -5.0F, 10.0F, 30.0F, 10.0F),
                ModelTransform.of(0.0F, -55.0F, -31.0F, -2.4435F, 0.0F, 0.0F));

        return TexturedModelData.of(data, 512, 512);
    }

    @Override
    public void setAngles(HollowTitanEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // rise out of the ground during the first moments
        float p = MathHelper.clamp(animationProgress / (float) HollowTitanEntity.RISE_TICKS, 0.0f, 1.0f);
        float eased = p * p * (3.0f - 2.0f * p);
        this.root.pivotY = (1.0f - eased) * 80.0f;

        // lean back a little when it attacks, and breathe slowly
        float t = Math.min(1.0f, entity.getAnim() / 12.0f);
        this.bbMain.pitch = -0.06f * t + 0.012f * MathHelper.sin(animationProgress * 0.07f);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
