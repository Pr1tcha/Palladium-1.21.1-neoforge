package net.threetag.palladium.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRenderer.class)
public interface PlayerRendererInvoker {

    @Invoker("setupRotations")
    void invokeSetupRotations(AbstractClientPlayer entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale);

    @Invoker("scale")
    void invokeScale(AbstractClientPlayer livingEntity, PoseStack poseStack, float partialTickTime);

}
