package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

@SuperBuilder(toBuilder = true)
public final class CardOpLog extends TransactionLog {
    @NonNull
    private final String card;
    @NonNull
    private final String cardHolder;
    @NonNull
    private final String account;

    @Override
    public Type getType() {
        return Type.CARD_OPERATION;
    }
}
