package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import net.threetag.palladium.client.model.animation.AnimationUtil;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.event.PalladiumClientEvents;
import net.threetag.palladiumcore.registry.client.OverlayRegistry;

public class PalladiumKubeJSClientPlugin implements KubeJSPlugin {

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(PalladiumClientJSEvents.GROUP);
    }

    @Override
    public void init() {
        PalladiumClientEvents.REGISTER_ANIMATIONS.register(registry -> {
            PalladiumJSEvents.REGISTER_ANIMATIONS.post(new RegisterAnimationsEventJS(registry));
            PalladiumClientJSEvents.REGISTER_ANIMATIONS.post(new RegisterAnimationsEventJS(registry));
            PalladiumJSEvents.REGISTER_GUI_OVERLAYS.post(new RegisterGuiOverlaysEventJS());
            PalladiumClientJSEvents.REGISTER_GUI_OVERLAYS.post(new RegisterGuiOverlaysEventJS());
        });

        PalladiumClientEvents.RENDER_POWER_SCREEN.register((screen, guiGraphics, mouseX, mouseY, partialTick, tab) -> {
            PalladiumJSEvents.RENDER_POWER_SCREEN.post(new RenderPowerScreenEventJS(screen, guiGraphics, mouseX, mouseY, partialTick, tab));
            PalladiumClientJSEvents.RENDER_POWER_SCREEN.post(new RenderPowerScreenEventJS(screen, guiGraphics, mouseX, mouseY, partialTick, tab));
        });

        OverlayRegistry.registerOverlay("palladium/kube_js_overlays", new RegisterGuiOverlaysEventJS.Overlay());
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        if (bindings.type().isClient()) {
            bindings.add("palladium", new PalladiumBindingClient());
            bindings.add("animationUtil", AnimationUtil.class);
            bindings.add("guiUtil", GuiUtilJS.class);
        }
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        if (registry.scriptType().isClient()) {
            registry.register(PalladiumAnimation.PlayerModelPart.class,
                    o -> PalladiumAnimation.PlayerModelPart.fromName(o.toString()));
        }
    }

}
