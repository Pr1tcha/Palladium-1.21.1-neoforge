package net.threetag.palladiumcore.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NetworkRegistration {

    private static final List<NetworkManager> MANAGERS = new CopyOnWriteArrayList<>();
    private static boolean installed;

    private NetworkRegistration() {
    }

    static void track(NetworkManager manager) {
        MANAGERS.add(manager);
    }

    public static synchronized void register(IEventBus modEventBus) {
        if (installed) {
            return;
        }
        installed = true;
        modEventBus.addListener(NetworkRegistration::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        MANAGERS.forEach(manager -> manager.registerPayloads(registrar));
    }
}
