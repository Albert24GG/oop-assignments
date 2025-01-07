package org.poo.bank.eventSystem;

import lombok.Getter;
import lombok.Setter;

public class BankEventListener<T> {
    /**
     * The consumer of the event
     */
    @Setter
    private BankEventHandler<T> handler;

    /**
     * The event listened for
     */
    @Getter
    private final Class<T> eventClass;

    /**
     * Create a new event listener
     *
     * @param eventClass the event class to listen for
     * @param handler    the handler for the event
     */
    public BankEventListener(final Class<T> eventClass, final BankEventHandler<T> handler) {
        this.eventClass = eventClass;
        this.handler = handler;
    }

    /**
     * Handle an event
     *
     * @param event the event to handle
     */
    public void handleEvent(final T event) {
        handler.handleEvent(event);
    }
}
