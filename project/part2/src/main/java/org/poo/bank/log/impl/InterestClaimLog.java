package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.InterestClaimLogView;
import org.poo.bank.type.Currency;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestClaimLog extends AuditLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final Currency currency;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), InterestClaimLogView.builder()
                .amount(amount)
                .currency(currency));
    }
}
