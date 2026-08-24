package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PalladiumJSEvents {

    EventGroup GROUP = EventGroup.of("PalladiumEvents");

    EventHandler CUSTOM_PROJECTILE_TICK = GROUP.server("customProjectileTick", () -> ProjectileTickEventJS.class);
    EventHandler REGISTER_PROPERTIES = GROUP.server("registerProperties", () -> RegisterPalladiumPropertyEventJS.class);
    EventHandler CLIENT_REGISTER_PROPERTIES = GROUP.client("registerPropertiesClientSided", () -> RegisterPalladiumPropertyEventJS.class);

}
