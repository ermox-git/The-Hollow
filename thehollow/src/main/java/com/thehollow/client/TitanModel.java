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

/** Upper half of a huge Watcher: wide shoulders, square head, spiked brow, no legs. */
public class TitanModel extends SinglePartEntityModel<HollowTitanEntity> {
    public static final EntityModelLayer LAYER =
            new EntityModelLayer(Identifier.of(TheHollow.MOD_ID, "hollow_titan"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public TitanModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild("body",
                ModelPartBuilder.create().uv(0, 60).cuboid(-14.0f, 0.0f, -7.0f, 28.0f, 40.0f, 14.0f),
                ModelTransform.pivot(0.0f, -16.0f, 0.0f));

        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -18.0f, -9.0f, 18.0f, 18.0f, 18.0f),
                ModelTransform.pivot(0.0f, -17.0f, 0.0f));
        head.addChild("jaw",
                ModelPartBuilder.create().uv(0, 36).cuboid(-7.0f, 0.0f, -7.0f, 14.0f, 8.0f, 14.0f),
                ModelTransform.pivot(0.0f, -1.0f, 0.0f));
        ModelPartBuilder spike = ModelPartBuilder.create().uv(72, 0).cuboid(-2.0f, -12.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        head.addChild("spike_left", spike, ModelTransform.pivot(-5.0f, -18.0f, 0.0f));
        head.addChild("spike_mid", spike, ModelTransform.pivot(0.0f, -18.0f, 0.0f));
        head.addChild("spike_right", spike, ModelTransform.pivot(5.0f, -18.0f, 0.0f));

        ModelPartData rightArm = root.addChild("right_arm",
                ModelPartBuilder.create().uv(88, 0).cuboid(-5.0f, 0.0f, -5.0f, 10.0f, 34.0f, 10.0f),
                ModelTransform.pivot(-19.0f, -13.0f, 0.0f));
        ModelPartData leftArm = root.addChild("left_arm",
                ModelPartBuilder.create().uv(88, 0).cuboid(-5.0f, 0.0f, -5.0f, 10.0f, 34.0f, 10.0f),
                ModelTransform.pivot(19.0f, -13.0f, 0.0f));
        ModelPartBuilder shoulderSpike = ModelPartBuilder.create().uv(72, 0).cuboid(-2.0f, -10.0f, -2.0f, 4.0f, 10.0f, 4.0f);
        rightArm.addChild("shoulder_spike", shoulderSpike, ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        leftArm.addChild("shoulder_spike", shoulderSpike, ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(data, 128, 128);
    }

    @Override
    public void setAngles(HollowTitanEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float rad = (float) Math.PI / 180.0f;

        // rise out of the ground during the first moments
        float p = MathHelper.clamp(animationProgress / (float) HollowTitanEntity.RISE_TICKS, 0.0f, 1.0f);
        float eased = p * p * (3.0f - 2.0f * p);
        this.root.pivotY = (1.0f - eased) * 80.0f;

        this.head.yaw = headYaw * rad;
        this.head.pitch = headPitch * rad + 0.04f * MathHelper.sin(animationProgress * 0.1f);

        float t = Math.min(1.0f, entity.getAnim() / 12.0f);
        float idle = 0.06f * MathHelper.sin(animationProgress * 0.08f);

        this.jaw.pitch = 0.2f + 0.15f * MathHelper.sin(animationProgress * 0.08f) + 0.9f * t;
        this.rightArm.pitch = idle * (1.0f - t) - 2.6f * t;
        this.leftArm.pitch = -idle * (1.0f - t) - 2.6f * t;
        this.rightArm.roll = 0.1f + 0.5f * t;
        this.leftArm.roll = -0.1f - 0.5f * t;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
