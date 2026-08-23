package net.threetag.palladiumcore.event;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public interface ScreenEvents {

    Event<Opening> OPENING = new Event<>(Opening.class, listeners -> (current, next) ->
            Event.result(listeners, listener -> listener.screenOpening(current, next)));
    Event<InitPre> INIT_PRE = new Event<>(InitPre.class, listeners -> screen ->
            Event.result(listeners, listener -> listener.screenInitPre(screen)));
    Event<InitPost> INIT_POST = new Event<>(InitPost.class, listeners -> screen ->
            listeners.forEach(listener -> listener.screenInitPost(screen)));

    @FunctionalInterface interface Opening { EventResult screenOpening(@Nullable Screen currentScreen, AtomicReference<Screen> newScreen); }
    @FunctionalInterface interface InitPre { EventResult screenInitPre(Screen screen); }
    @FunctionalInterface interface InitPost { void screenInitPost(Screen screen); }
}
