package org.poo.bank.transaction;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class TransactionLog {
    @NonNull
    private final Integer timestamp;
    private final String description;
    private final String error;

    /**
     * Get the type of the transaction
     *
     * @return the type of the transaction
     */
    public abstract TransactionLogType getType();

    /**
     * Convert the transaction log to a view
     *
     * @return the transaction log view
     */
    public TransactionLogView toView() {
        return TransactionLogView.builder()
                .timestamp(timestamp)
                .description(description)
                .error(error)
                .type(getType())
                .build();
    }
}
