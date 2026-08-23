package net.threetag.palladiumcore.registry.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class ClientRegistrationEvents {

    private static boolean registered;

    private ClientRegistrationEvents() {
    }

    public static synchronized void register(IEventBus modEventBus) {
        if (registered) {
            return;
        }
        registered = true;

        modEventBus.addListener(BlockEntityRendererRegistry::registerRenderers);
        modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> ColorHandlerRegistry.registerItemColors(event));
        modEventBus.addListener((RegisterColorHandlersEvent.Block event) -> ColorHandlerRegistry.registerBlockColors(event));
        modEventBus.addListener(EntityRendererRegistry::registerRenderers);
        modEventBus.addListener(EntityRendererRegistry::registerModelLayers);
        modEventBus.addListener(EntityRendererRegistry::addRenderLayers);
        modEventBus.addListener(KeyMappingRegistry::registerMappings);
        modEventBus.addListener(OverlayRegistry::registerOverlays);
        modEventBus.addListener(ParticleProviderRegistry::registerProviders);
        modEventBus.addListener(ClientRegistrationEvents::registerReloadListeners);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        net.threetag.palladiumcore.registry.ReloadListenerRegistry.clientListeners()
                .forEach(event::registerReloadListener);
    }
}
