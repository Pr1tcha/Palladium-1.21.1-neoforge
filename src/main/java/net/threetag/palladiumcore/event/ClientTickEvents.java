package net.threetag.palladiumcore.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public interface ClientTickEvents {

    Event<ClientTick> CLIENT_PRE = clientTickEvent();
    Event<ClientTick> CLIENT_POST = clientTickEvent();
    Event<ClientLevelTick> CLIENT_LEVEL_PRE = clientLevelTickEvent();
    Event<ClientLevelTick> CLIENT_LEVEL_POST = clientLevelTickEvent();

    private static Event<ClientTick> clientTickEvent() {
        return new Event<>(ClientTick.class, listeners -> minecraft ->
                listeners.forEach(listener -> listener.clientTick(minecraft)));
    }

    private static Event<ClientLevelTick> clientLevelTickEvent() {
        return new Event<>(ClientLevelTick.class, listeners -> (minecraft, level) ->
                listeners.forEach(listener -> listener.clientLevelTick(minecraft, level)));
    }

    @FunctionalInterface interface ClientTick { void clientTick(Minecraft minecraft); }
    @FunctionalInterface interface ClientLevelTick { void clientLevelTick(Minecraft minecraft, ClientLevel clientLevel); }
}
