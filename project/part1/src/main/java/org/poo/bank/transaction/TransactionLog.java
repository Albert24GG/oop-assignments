package org.poo.bank.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder(toBuilder = true)
@Getter
public abstract class TransactionLog {
    @NonNull
    private final Integer timestamp;
    private final String description;
    private final String error;

    public enum Type {
        GENERIC,
        FAILED,
        ACCOUNT_CREATION,
        CARD_OPERATION,
        INTEREST_OPERATION,
        CARD_PAYMENT,
        SPLIT_PAYMENT,
        TRANSFER
    }

    /**
     * Get the type of the transaction
     *
     * @return the type of the transaction
     */
    @JsonIgnore
    public abstract Type getType();
}