package net.threetag.palladiumcore.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Small priority-aware event used by the legacy Palladium API. */
public final class Event<T> {

    private final Map<T, Priority> handlers = new IdentityHashMap<>();
    private final Function<List<T>, T> multiplexer;
    private T invoker;

    public Event(Class<T> handlerClass, Function<List<T>, T> multiplexer) {
        this.multiplexer = multiplexer;
        update();
    }

    public void register(T handler) {
        register(Priority.NORMAL, handler);
    }

    public synchronized void register(Priority priority, T handler) {
        handlers.put(handler, priority);
        update();
    }

    private void update() {
        List<T> listeners = new ArrayList<>(handlers.keySet());
        listeners.sort(Comparator.comparingInt(value -> handlers.get(value).ordinal()));
        invoker = multiplexer.apply(List.copyOf(listeners));
    }

    public T invoker() {
        return invoker;
    }

    public static <T> EventResult result(List<T> listeners, Function<T, EventResult> function) {
        boolean cancel = false;
        for (T listener : listeners) {
            EventResult result = function.apply(listener);
            cancel |= result.cancelsEvent();
            if (result.stopsListeners()) {
                break;
            }
        }
        return cancel ? EventResult.cancel() : EventResult.pass();
    }

    public enum Priority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }
}
