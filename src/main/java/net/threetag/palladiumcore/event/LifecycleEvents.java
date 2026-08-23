package net.threetag.palladiumcore.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;

public interface LifecycleEvents {

    Event<Runnable> SETUP = runnableEvent();
    Event<Runnable> CLIENT_SETUP = runnableEvent();
    Event<Server> SERVER_ABOUT_TO_START = serverEvent();
    Event<Server> SERVER_STARTING = serverEvent();
    Event<Server> SERVER_STARTED = serverEvent();
    Event<Server> SERVER_STOPPING = serverEvent();
    Event<Server> SERVER_STOPPED = serverEvent();
    Event<DatapackSync> DATAPACK_SYNC = new Event<>(DatapackSync.class, listeners -> (list, player) ->
            listeners.forEach(listener -> listener.onDatapackSync(list, player)));

    private static Event<Runnable> runnableEvent() {
        return new Event<>(Runnable.class, listeners -> () -> listeners.forEach(Runnable::run));
    }

    private static Event<Server> serverEvent() {
        return new Event<>(Server.class, listeners -> server -> listeners.forEach(listener -> listener.server(server)));
    }

    @FunctionalInterface
    interface Server {
        void server(MinecraftServer server);
    }

    @FunctionalInterface
    interface DatapackSync {
        void onDatapackSync(PlayerList playerList, @Nullable ServerPlayer player);
    }
}
