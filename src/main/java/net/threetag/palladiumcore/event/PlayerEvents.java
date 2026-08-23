package net.threetag.palladiumcore.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface PlayerEvents {

    Event<Join> JOIN = joinEvent();
    Event<Quit> QUIT = quitEvent();
    Event<Join> CLIENT_JOIN = joinEvent();
    Event<Quit> CLIENT_QUIT = quitEvent();
    Event<Clone> CLONE = new Event<>(Clone.class, listeners -> (oldPlayer, newPlayer, wasDeath) ->
            listeners.forEach(listener -> listener.playerClone(oldPlayer, newPlayer, wasDeath)));
    Event<Respawn> RESPAWN = new Event<>(Respawn.class, listeners -> (player, endConquered) ->
            listeners.forEach(listener -> listener.playerRespawn(player, endConquered)));
    Event<ChangedDimension> CHANGED_DIMENSION = new Event<>(ChangedDimension.class, listeners -> (player, destination) ->
            listeners.forEach(listener -> listener.playerChangedDimension(player, destination)));
    Event<NameFormat> NAME_FORMAT = new Event<>(NameFormat.class, listeners -> (player, username, displayName) ->
            listeners.forEach(listener -> listener.playerNameFormat(player, username, displayName)));
    Event<Tracking> START_TRACKING = trackingEvent();
    Event<Tracking> STOP_TRACKING = trackingEvent();
    Event<AnvilUpdate> ANVIL_UPDATE = new Event<>(AnvilUpdate.class, listeners ->
            (player, left, right, name, cost, materialCost, output) -> Event.result(listeners,
                    listener -> listener.anvilUpdate(player, left, right, name, cost, materialCost, output)));

    private static Event<Join> joinEvent() {
        return new Event<>(Join.class, listeners -> player -> listeners.forEach(listener -> listener.playerJoin(player)));
    }

    private static Event<Quit> quitEvent() {
        return new Event<>(Quit.class, listeners -> player -> listeners.forEach(listener -> listener.playerQuit(player)));
    }

    private static Event<Tracking> trackingEvent() {
        return new Event<>(Tracking.class, listeners -> (tracker, tracked) ->
                listeners.forEach(listener -> listener.playerTracking(tracker, tracked)));
    }

    @FunctionalInterface interface Join { void playerJoin(Player player); }
    @FunctionalInterface interface Quit { void playerQuit(Player player); }
    @FunctionalInterface interface Clone { void playerClone(Player oldPlayer, Player newPlayer, boolean wasDeath); }
    @FunctionalInterface interface Respawn { void playerRespawn(Player player, boolean endConquered); }
    @FunctionalInterface interface ChangedDimension { void playerChangedDimension(Player player, ResourceKey<Level> destination); }
    @FunctionalInterface interface NameFormat { void playerNameFormat(Player player, Component username, AtomicReference<Component> displayName); }
    @FunctionalInterface interface Tracking { void playerTracking(Player tracker, Entity trackedEntity); }

    @FunctionalInterface
    interface AnvilUpdate {
        EventResult anvilUpdate(Player player, ItemStack left, ItemStack right, @Nullable String name,
                                AtomicInteger cost, AtomicInteger materialCost, AtomicReference<ItemStack> output);
    }
}
