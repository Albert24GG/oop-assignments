package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.TransactionLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLog extends TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final String merchant;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.CARD_PAYMENT;
    }

    @Override
    public TransactionLogView toView() {
        return super.toView().toBuilder()
                .amountAsDouble(amount)
                .merchant(merchant)
                .build();
    }
}
