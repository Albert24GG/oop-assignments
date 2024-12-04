package org.poo.bank.transaction.impl;

import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;


@SuperBuilder(toBuilder = true)
public final class TransferLog extends TransactionLog {
    private final String senderIban;
    private final String receiverIban;
    private final Double amount;
    private final String transferType;

    @Override
    public Type getType() {
        return Type.TRANSFER;
    }
}
