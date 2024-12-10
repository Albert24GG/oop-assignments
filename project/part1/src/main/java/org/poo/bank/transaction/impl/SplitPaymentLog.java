package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@SuperBuilder(toBuilder = true)
public final class SplitPaymentLog extends TransactionLog {
    @NonNull
    private final Currency currency;
    @NonNull
    private final Double amount;
    @NonNull
    private final List<IBAN> involvedAccounts;

    @Override
    public Type getType() {
        return Type.SPLIT_PAYMENT;
    }
}
