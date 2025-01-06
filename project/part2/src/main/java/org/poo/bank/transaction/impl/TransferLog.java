package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.TransferLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;


@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLog extends TransactionLog {
    @NonNull
    private final IBAN senderIBAN;
    @NonNull
    private final IBAN receiverIBAN;
    @NonNull
    private final Double amount;
    @NonNull
    private final Currency currency;
    @NonNull
    private final String transferType;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.TRANSFER;
    }

    @Override
    public TransactionLogView toView() {
        return TransferLogView.builder()
                .timestamp(getTimestamp())
                .description(getDescription())
                .error(getError())
                .type(getType())
                .senderIBAN(senderIBAN)
                .receiverIBAN(receiverIBAN)
                .amount(amount)
                .currency(currency)
                .transferType(transferType)
                .build();
    }
}
