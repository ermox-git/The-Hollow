package com.thehollow.client;

import com.thehollow.TheHollow;
import com.thehollow.entity.WatcherEntity;
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

/** Tall, gaunt figure: tiny cocked head with a hanging jaw, ribs, and arms that reach the knees. */
public class WatcherModel extends SinglePartEntityModel<WatcherEntity> {
    public static final EntityModelLayer LAYER =
            new EntityModelLayer(Identifier.of(TheHollow.MOD_ID, "watcher"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public WatcherModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                ModelTransform.pivot(0.0f, -12.0f, 0.0f));
        head.addChild("jaw",
                ModelPartBuilder.create().uv(32, 0).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 4.0f, 6.0f),
                ModelTransform.pivot(0.0f, -1.0f, 0.0f));

        root.addChild("body",
                ModelPartBuilder.create().uv(0, 16).cuboid(-3.0f, 0.0f, -1.5f, 6.0f, 14.0f, 3.0f),
                ModelTransform.pivot(0.0f, -12.0f, 0.0f));

        root.addChild("right_arm",
                ModelPartBuilder.create().uv(24, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 24.0f, 2.0f),
                ModelTransform.pivot(-4.0f, -11.0f, 0.0f));
        root.addChild("left_arm",
                ModelPartBuilder.create().uv(24, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 24.0f, 2.0f),
                ModelTransform.pivot(4.0f, -11.0f, 0.0f));

        root.addChild("right_leg",
                ModelPartBuilder.create().uv(44, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 22.0f, 2.0f),
                ModelTransform.pivot(-1.5f, 2.0f, 0.0f));
        root.addChild("left_leg",
                ModelPartBuilder.create().uv(44, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 22.0f, 2.0f),
                ModelTransform.pivot(1.5f, 2.0f, 0.0f));

        return TexturedModelData.of(data, 64, 64);
    }

    @Override
    public void setAngles(WatcherEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float rad = (float) Math.PI / 180.0f;
        int state = entity.getRageState();

        this.head.yaw = headYaw * rad;
        this.head.pitch = headPitch * rad;
        this.head.roll = 0.28f; // permanently cocked head
        this.jaw.pitch = 0.35f + 0.12f * MathHelper.sin(animationProgress * 0.15f);

        float swing = MathHelper.cos(limbAngle * 0.6662f) * 1.2f * limbDistance;
        this.rightLeg.pitch = swing;
        this.leftLeg.pitch = -swing;
        this.rightArm.pitch = -swing * 0.4f;
        this.leftArm.pitch = swing * 0.4f;
        this.rightArm.roll = 0.06f + 0.04f * MathHelper.cos(animationProgress * 0.09f);
        this.leftArm.roll = -0.06f - 0.04f * MathHelper.cos(animationProgress * 0.09f + 1.3f);

        if (state == WatcherEntity.ROARING) {
            // head thrown back, jaw wide, arms flung up and trembling
            float shake = MathHelper.sin(animationProgress * 1.8f) * 0.12f;
            this.head.pitch = -0.9f;
            this.head.roll = shake;
            this.jaw.pitch = 1.2f + shake;
            this.rightArm.pitch = -2.7f + shake;
            this.leftArm.pitch = -2.7f - shake;
            this.rightArm.roll = 0.6f;
            this.leftArm.roll = -0.6f;
        } else if (state == WatcherEntity.ENRAGED) {
            // arms reaching forward, jaw hanging open, faster stride
            float claw = MathHelper.sin(animationProgress * 0.9f) * 0.15f;
            this.head.roll = 0.0f;
            this.jaw.pitch = 0.9f;
            this.rightArm.pitch = -1.5f + claw;
            this.leftArm.pitch = -1.5f - claw;
            this.rightArm.roll = 0.1f;
            this.leftArm.roll = -0.1f;
            float run = MathHelper.cos(limbAngle * 0.9f) * 1.5f * limbDistance;
            this.rightLeg.pitch = run;
            this.leftLeg.pitch = -run;
        }
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
