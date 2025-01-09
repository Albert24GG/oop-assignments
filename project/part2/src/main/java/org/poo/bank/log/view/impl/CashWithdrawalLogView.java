package org.poo.bank.log.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.view.AuditLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CashWithdrawalLogView extends AuditLogView {
    private final double amount;
}
