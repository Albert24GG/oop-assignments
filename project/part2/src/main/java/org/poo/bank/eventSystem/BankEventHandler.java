package org.poo.bank.eventSystem;

@FunctionalInterface
public interface BankEventHandler<T> {
    /**
     * Handle an event
     *
     * @param event the event to handle
     */
    void handleEvent(T event);
}
