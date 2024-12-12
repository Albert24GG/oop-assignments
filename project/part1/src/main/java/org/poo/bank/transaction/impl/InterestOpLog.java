package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestOpLog extends TransactionLog {
    @Override
    public Type getType() {
        return Type.INTEREST_OPERATION;
    }
}
