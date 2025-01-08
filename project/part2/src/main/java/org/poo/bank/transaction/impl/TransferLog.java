package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.transaction.view.impl.TransferLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;


@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLog extends AuditLog {
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
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), TransferLogView.builder()
                .senderIBAN(senderIBAN)
                .receiverIBAN(receiverIBAN)
                .amount(amount)
                .currency(currency)
                .transferType(transferType));
    }
}
