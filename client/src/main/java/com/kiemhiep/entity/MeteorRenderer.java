package com.kiemhiep.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/**
 * Renderer cho MeteorEntity: vẽ quả cầu thiên thạch bằng texture billboard (luôn hướng camera).
 * Texture: assets/kiemhiep/textures/entity/meteor.png
 */
public class MeteorRenderer extends EntityRenderer<MeteorEntity, EntityRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(com.kiemhiep.KiemhiepConstants.MOD_ID, "textures/entity/meteor.png");
    private static final float SCALE = 2.5f;
    private static final float HALF = 0.5f;

    public MeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(
        EntityRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector collector,
        CameraRenderState cameraState
    ) {
        if (state.isInvisible) return;

        poseStack.pushPose();
        Vec3 offset = getRenderOffset(state);
        poseStack.translate(offset.x, offset.y, offset.z);
        // Billboard: xoay quad theo camera để luôn hướng về người chơi
        Quaternionf orientation = cameraState.orientation;
        poseStack.mulPose(new Quaternionf(orientation));
        poseStack.scale(SCALE, SCALE, SCALE);

        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        RenderType renderType = RenderTypes.entityCutoutNoCull(TEXTURE);
        collector.order(0).submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            float x0 = -HALF;
            float x1 = HALF;
            float y0 = -HALF;
            float y1 = HALF;
            vertexConsumer.addVertex(pose.pose(), x0, y1, 0).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(pose, 0f, 0f, 1f);
            vertexConsumer.addVertex(pose.pose(), x1, y1, 0).setColor(1f, 1f, 1f, 1f).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pose, 0f, 0f, 1f);
            vertexConsumer.addVertex(pose.pose(), x1, y0, 0).setColor(1f, 1f, 1f, 1f).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pose, 0f, 0f, 1f);
            vertexConsumer.addVertex(pose.pose(), x0, y0, 0).setColor(1f, 1f, 1f, 1f).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pose, 0f, 0f, 1f);
        });

        poseStack.popPose();
    }
}
