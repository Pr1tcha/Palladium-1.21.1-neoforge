package net.threetag.palladiumcore.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.List;

public final class ReloadListenerRegistry {

    private static final List<PreparableReloadListener> SERVER_LISTENERS = new ArrayList<>();
    private static final List<PreparableReloadListener> CLIENT_LISTENERS = new ArrayList<>();

    private ReloadListenerRegistry() {
    }

    public static void register(PackType packType, ResourceLocation ignoredId, PreparableReloadListener listener) {
        if (packType == PackType.SERVER_DATA) {
            SERVER_LISTENERS.add(listener);
        } else if (packType == PackType.CLIENT_RESOURCES) {
            CLIENT_LISTENERS.add(listener);
        }
    }

    static void addServerListeners(AddReloadListenerEvent event) {
        SERVER_LISTENERS.forEach(event::addListener);
    }

    public static List<PreparableReloadListener> clientListeners() {
        return List.copyOf(CLIENT_LISTENERS);
    }
}
