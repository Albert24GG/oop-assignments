package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.IBAN;

@SuperBuilder(toBuilder = true)
public final class SavingsWithdrawLog extends TransactionLog {
    @NonNull
    private final Double amount;
    private final IBAN savingsAccountIBAN;
    private final IBAN classicAccountIBAN;


    @Override
    public TransactionLogType getType() {
        return TransactionLogType.SAVINGS_WITHDRAW;
    }

    @Override
    public TransactionLogView toView() {
        return super.toView().toBuilder()
                .amountAsDouble(amount)
                .savingsAccountIBAN(savingsAccountIBAN)
                .classicAccountIBAN(classicAccountIBAN)
                .build();
    }
}
