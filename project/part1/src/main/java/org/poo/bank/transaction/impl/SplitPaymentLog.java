package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

import java.util.List;

@SuperBuilder(toBuilder = true)
public final class SplitPaymentLog extends TransactionLog {
    @NonNull
    private final String currency;
    @NonNull
    public final Double amount;
    @NonNull
    private final List<String> involvedAccounts;

    @Override
    public Type getType() {
        return Type.SPLIT_PAYMENT;
    }
}
