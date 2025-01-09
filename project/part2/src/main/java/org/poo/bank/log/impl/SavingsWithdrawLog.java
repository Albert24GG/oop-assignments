package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.SavingsWithdrawLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class SavingsWithdrawLog extends AuditLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final IBAN savingsAccountIBAN;
    @NonNull
    private final IBAN classicAccountIBAN;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), SavingsWithdrawLogView.builder()
                .amount(amount)
                .savingsAccountIBAN(savingsAccountIBAN)
                .classicAccountIBAN(classicAccountIBAN));
    }
}
