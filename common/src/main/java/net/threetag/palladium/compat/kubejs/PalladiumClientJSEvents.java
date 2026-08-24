package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PalladiumClientJSEvents {

    EventGroup GROUP = EventGroup.of("PalladiumClientEvents");

    EventHandler REGISTER_ANIMATIONS = GROUP.client("registerAnimations", () -> RegisterAnimationsEventJS.class);
    EventHandler REGISTER_GUI_OVERLAYS = GROUP.client("registerGuiOverlays", () -> RegisterGuiOverlaysEventJS.class);
    EventHandler RENDER_POWER_SCREEN = GROUP.client("renderPowerScreen", () -> RenderPowerScreenEventJS.class);

}
