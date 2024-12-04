package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

@SuperBuilder
public final class PaymentLog extends TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final String commerciant;

    @Override
    public Type getType() {
        return Type.PAYMENT;
    }
}
