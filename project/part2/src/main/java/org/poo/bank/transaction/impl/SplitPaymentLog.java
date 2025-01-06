package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.SplitPaymentLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public final class SplitPaymentLog extends TransactionLog {
    @NonNull
    private final Currency currency;
    @NonNull
    private final Double amount;
    @NonNull
    private final List<IBAN> involvedAccounts;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.SPLIT_PAYMENT;
    }

    @Override
    public TransactionLogView toView() {
        return SplitPaymentLogView.builder()
                .timestamp(getTimestamp())
                .description(getDescription())
                .error(getError())
                .type(getType())
                .currency(currency)
                .amount(amount)
                .involvedAccounts(List.copyOf(involvedAccounts))
                .build();
    }
}
