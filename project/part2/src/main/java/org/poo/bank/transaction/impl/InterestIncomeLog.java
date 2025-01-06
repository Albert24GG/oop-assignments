package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.InterestIncomeLogView;
import org.poo.bank.type.Currency;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestIncomeLog extends TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final Currency currency;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.INTEREST_INCOME;
    }

    @Override
    public TransactionLogView toView() {
        return InterestIncomeLogView.builder()
                .timestamp(getTimestamp())
                .type(getType())
                .description(getDescription())
                .error(getError())
                .amount(amount)
                .currency(currency)
                .build();
    }
}
