package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.type.IBAN;


@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLog extends TransactionLog {
    private final IBAN senderIBAN;
    private final IBAN receiverIBAN;
    private final String amount;
    private final String transferType;

    @Override
    public Type getType() {
        return Type.TRANSFER;
    }
}
