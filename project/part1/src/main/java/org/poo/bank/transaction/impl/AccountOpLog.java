package org.poo.bank.transaction.impl;

import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;

@SuperBuilder(toBuilder = true)
public final class AccountOpLog extends TransactionLog {
    @Override
    public TransactionLogType getType() {
        return TransactionLogType.ACCOUNT_CREATION;
    }
}
