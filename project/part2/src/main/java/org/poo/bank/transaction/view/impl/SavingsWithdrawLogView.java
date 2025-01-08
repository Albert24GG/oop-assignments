package org.poo.bank.transaction.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class SavingsWithdrawLogView extends AuditLogView {
    private final Double amount;
    private final IBAN savingsAccountIBAN;
    private final IBAN classicAccountIBAN;
}
