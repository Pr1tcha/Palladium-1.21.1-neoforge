package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public final class KeyMappingRegistry {

    private static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();

    private KeyMappingRegistry() {
    }

    public static void register(KeyMapping mapping) {
        KEY_MAPPINGS.add(mapping);
    }

    static void registerMappings(RegisterKeyMappingsEvent event) {
        KEY_MAPPINGS.forEach(event::register);
    }
}
