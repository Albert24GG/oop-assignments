package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.CashWithdrawalLogView;
import org.poo.bank.type.Location;

@Getter
@SuperBuilder(toBuilder = true)
public final class CashWithdrawLog extends TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final Location location;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.CASH_WITHDRAWAL;
    }

    @Override
    public TransactionLogView toView() {
        return CashWithdrawalLogView.builder()
                .description(getDescription())
                .timestamp(getTimestamp())
                .error(getError())
                .type(getType())
                .amount(amount)
                .build();
    }
}
