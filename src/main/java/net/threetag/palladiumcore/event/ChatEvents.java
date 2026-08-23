package net.threetag.palladiumcore.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public interface ChatEvents {

    Event<ServerSubmitted> SERVER_SUBMITTED = new Event<>(ServerSubmitted.class, listeners ->
            (player, rawMessage, message) -> Event.result(listeners,
                    listener -> listener.chatMessageSubmitted(player, rawMessage, message)));

    @FunctionalInterface
    interface ServerSubmitted {
        EventResult chatMessageSubmitted(ServerPlayer player, String rawMessage, Component message);
    }
}
