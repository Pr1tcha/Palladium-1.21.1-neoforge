package net.threetag.palladium.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.threetag.palladium.client.entity.effect.EnergyBeamEffectClient;
import net.threetag.palladium.entity.EffectEntity;
import net.threetag.palladium.entity.effect.EnergyBeamEffect;

public class EffectEntityRenderer extends EntityRenderer<EffectEntity> {

    public EffectEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EffectEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity anchor = entity.getAnchorEntity();

        if (anchor != null) {
            poseStack.pushPose();
            if (entity.entityEffect instanceof EnergyBeamEffect energyBeamEffect) {
                EnergyBeamEffectClient.render(energyBeamEffect, entity, anchor, poseStack, buffer, packedLight, Minecraft.getInstance().player == anchor && Minecraft.getInstance().options.getCameraType().isFirstPerson(), partialTicks);
            }
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EffectEntity entity) {
        return null;
    }
}
