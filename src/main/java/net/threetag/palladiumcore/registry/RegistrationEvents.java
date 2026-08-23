package net.threetag.palladiumcore.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class RegistrationEvents {

    private static boolean registered;

    private RegistrationEvents() {
    }

    public static synchronized void register(IEventBus modEventBus) {
        if (registered) {
            return;
        }
        registered = true;

        modEventBus.addListener(CreativeModeTabRegistry::buildContents);
        modEventBus.addListener(EntityAttributeRegistry::createAttributes);
        modEventBus.addListener(EntityAttributeRegistry::modifyAttributes);

        NeoForge.EVENT_BUS.addListener(ReloadListenerRegistry::addServerListeners);
        NeoForge.EVENT_BUS.addListener(VillagerTradeRegistry::addVillagerTrades);
        NeoForge.EVENT_BUS.addListener(VillagerTradeRegistry::addWandererTrades);
    }
}
