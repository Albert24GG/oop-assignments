package org.poo.bank.transaction.impl;

import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;

@SuperBuilder(toBuilder = true)
public final class GenericLog extends TransactionLog {
    @Override
    public Type getType() {
        return Type.GENERIC;
    }
}
