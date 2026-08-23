package net.threetag.palladiumcore.event;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;

import java.util.concurrent.atomic.AtomicReference;

public interface ViewportEvents {

    Event<ComputeCameraAngles> COMPUTE_CAMERA_ANGLES = new Event<>(ComputeCameraAngles.class, listeners ->
            (renderer, camera, partialTick, yaw, pitch, roll) -> listeners.forEach(listener ->
                    listener.computeCameraAngles(renderer, camera, partialTick, yaw, pitch, roll)));
    Event<RenderFog> RENDER_FOG = new Event<>(RenderFog.class, listeners ->
            (renderer, camera, partialTick, mode, type, far, near, shape) -> Event.result(listeners,
                    listener -> listener.renderFog(renderer, camera, partialTick, mode, type, far, near, shape)));
    Event<ComputeFogColor> COMPUTE_FOG_COLOR = new Event<>(ComputeFogColor.class, listeners ->
            (renderer, camera, partialTick, red, green, blue) -> listeners.forEach(listener ->
                    listener.computeFogColor(renderer, camera, partialTick, red, green, blue)));

    @FunctionalInterface
    interface ComputeCameraAngles {
        void computeCameraAngles(GameRenderer gameRenderer, Camera camera, double partialTick,
                                 AtomicReference<Float> yaw, AtomicReference<Float> pitch, AtomicReference<Float> roll);
    }

    @FunctionalInterface
    interface RenderFog {
        EventResult renderFog(GameRenderer gameRenderer, Camera camera, double partialTick,
                              FogRenderer.FogMode fogMode, FogType fogType,
                              AtomicReference<Float> farPlaneDistance,
                              AtomicReference<Float> nearPlaneDistance,
                              AtomicReference<FogShape> fogShape);
    }

    @FunctionalInterface
    interface ComputeFogColor {
        void computeFogColor(GameRenderer gameRenderer, Camera camera, double partialTick,
                             AtomicReference<Float> red, AtomicReference<Float> green, AtomicReference<Float> blue);
    }
}
