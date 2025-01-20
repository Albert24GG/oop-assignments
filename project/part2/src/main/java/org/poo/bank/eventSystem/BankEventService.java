package org.poo.bank.eventSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BankEventService {
    private final Map<Class<?>, List<BankEventListener<?>>>
            eventListeners = new HashMap<>();

    /**
     * Subscribe a listener to an event.
     *
     * @param listener the listener to subscribe
     */
    public void subscribe(final BankEventListener<?> listener) {
        Class<?> eventClass = listener.getEventClass();

        eventListeners.computeIfAbsent(eventClass, k -> new ArrayList<>())
                .add(listener);
    }

    /**
     * Post an event to all listeners.
     *
     * @param event the event to dispatch
     */
    public <T> void post(final T event) {
        eventListeners.getOrDefault(event.getClass(), List.of())
                .forEach(listener -> ((BankEventListener<T>) (listener)).handleEvent(event));
    }

    /**
     * Unsubscribe a listener from an event.
     *
     * @param listener the listener to unsubscribe
     */
    public void unsubscribe(final BankEventListener<?> listener) {
        List<BankEventListener<?>> listeners = eventListeners.get(listener.getEventClass());
        if (listeners != null) {
            listeners.remove(listener);
        }
    }
}
