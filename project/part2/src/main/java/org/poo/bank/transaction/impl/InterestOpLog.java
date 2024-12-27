package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestOpLog extends TransactionLog {
    @Override
    public TransactionLogType getType() {
        return TransactionLogType.INTEREST_OPERATION;
    }
}
