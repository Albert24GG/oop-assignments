package org.poo.bank.transaction.impl;

import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

@SuperBuilder(toBuilder = true)
public final class FailedOpLog extends TransactionLog {
    @Override
    public Type getType() {
        return Type.FAILED;
    }
}
