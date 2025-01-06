package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.SavingsWithdrawLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class SavingsWithdrawLog extends TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final IBAN savingsAccountIBAN;
    @NonNull
    private final IBAN classicAccountIBAN;


    @Override
    public TransactionLogType getType() {
        return TransactionLogType.SAVINGS_WITHDRAWAL;
    }

    @Override
    public TransactionLogView toView() {
        return SavingsWithdrawLogView.builder()
                .timestamp(getTimestamp())
                .description(getDescription())
                .error(getError())
                .type(getType())
                .amount(amount)
                .savingsAccountIBAN(savingsAccountIBAN)
                .classicAccountIBAN(classicAccountIBAN)
                .build();
    }
}
