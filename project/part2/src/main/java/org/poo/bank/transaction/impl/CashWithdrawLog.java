package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.Location;

@Getter
@SuperBuilder(toBuilder = true)
public final class CashWithdrawLog extends TransactionLog {
    private final double amount;
    private final Location location;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.CASH_WITHDRAW;
    }

    @Override
    public TransactionLogView toView() {
        return super.toView().toBuilder()
                .amountAsDouble(amount)
                .build();
    }
}
