package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.CardPaymentLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLog extends AuditLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final String merchant;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), CardPaymentLogView.builder()
                .amount(amount)
                .merchant(merchant));
    }
}
