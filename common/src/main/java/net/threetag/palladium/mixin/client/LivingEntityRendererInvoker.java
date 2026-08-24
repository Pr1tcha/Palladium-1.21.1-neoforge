package net.threetag.palladium.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererInvoker {

    @Invoker("getRenderType")
    RenderType palladium$getRenderType(LivingEntity entity, boolean bodyVisible, boolean translucent, boolean glowing);

    @Invoker("getWhiteOverlayProgress")
    float palladium$getWhiteOverlayProgress(LivingEntity entity, float partialTick);

    @Invoker("isBodyVisible")
    boolean palladium$isBodyVisible(LivingEntity entity);

    @Invoker("scale")
    void palladium$scale(LivingEntity entity, PoseStack poseStack, float partialTick);
}
