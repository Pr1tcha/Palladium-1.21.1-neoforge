package net.threetag.palladiumcore.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface ScreenEvents {

    Event<Opening> OPENING = new Event<>(Opening.class, listeners -> (current, next) ->
            Event.result(listeners, listener -> listener.screenOpening(current, next)));
    Event<InitPre> INIT_PRE = new Event<>(InitPre.class, listeners -> screen ->
            Event.result(listeners, listener -> listener.screenInitPre(screen)));
    Event<InitPost> INIT_POST = new Event<>(InitPost.class, listeners -> (screen, addListener) ->
            listeners.forEach(listener -> listener.screenInitPost(screen, addListener)));

    @FunctionalInterface interface Opening { EventResult screenOpening(@Nullable Screen currentScreen, AtomicReference<Screen> newScreen); }
    @FunctionalInterface interface InitPre { EventResult screenInitPre(Screen screen); }
    @FunctionalInterface interface InitPost { void screenInitPost(Screen screen, Consumer<GuiEventListener> addListener); }
}
