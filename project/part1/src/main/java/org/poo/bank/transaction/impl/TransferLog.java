package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.IBAN;


@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLog extends TransactionLog {
    private final IBAN senderIBAN;
    private final IBAN receiverIBAN;
    private final String amount;
    private final String transferType;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.TRANSFER;
    }

    @Override
    public TransactionLogView toView() {
        return super.toView().toBuilder()
                .senderIBAN(senderIBAN)
                .receiverIBAN(receiverIBAN)
                .amountAsString(amount)
                .transferType(transferType)
                .build();
    }
}
