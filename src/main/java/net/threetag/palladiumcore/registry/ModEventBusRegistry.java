package net.threetag.palladiumcore.registry;

import net.neoforged.bus.api.IEventBus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal replacement for PalladiumCore's mod event-bus lookup.
 *
 * <p>The legacy registration API exposes a no-argument {@code register()}
 * method. NeoForge requires the owning mod event bus, so the port records that
 * bus once in the mod constructor and keeps the old call sites unchanged.</p>
 */
public final class ModEventBusRegistry {

    private static final Map<String, IEventBus> MOD_EVENT_BUSES = new ConcurrentHashMap<>();

    private ModEventBusRegistry() {
    }

    public static void register(String modId, IEventBus eventBus) {
        Objects.requireNonNull(modId, "modId");
        Objects.requireNonNull(eventBus, "eventBus");

        IEventBus previous = MOD_EVENT_BUSES.putIfAbsent(modId, eventBus);
        if (previous != null && previous != eventBus) {
            throw new IllegalStateException("A different mod event bus is already registered for '" + modId + "'");
        }
    }

    static IEventBus get(String modId) {
        IEventBus eventBus = MOD_EVENT_BUSES.get(modId);
        if (eventBus == null) {
            throw new IllegalStateException("Mod '" + modId + "' did not register its NeoForge event bus before registry initialization");
        }
        return eventBus;
    }
}
