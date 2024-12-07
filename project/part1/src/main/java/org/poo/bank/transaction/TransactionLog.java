package org.poo.bank.transaction;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public abstract class TransactionLog {
    @NonNull
    private final Integer timestamp;
    private final String description;
    private final String error;

    public enum Type {
        GENERIC,
        ACCOUNT_CREATION,
        CARD_OPERATION,
        INTEREST_OPERATION,
        PAYMENT,
        SPLIT_PAYMENT,
        TRANSFER
    }

    /**
     * Get the type of the transaction
     *
     * @return the type of the transaction
     */
    public abstract Type getType();
}