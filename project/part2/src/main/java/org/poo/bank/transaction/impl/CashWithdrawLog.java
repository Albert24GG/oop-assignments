package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.transaction.view.impl.CashWithdrawalLogView;
import org.poo.bank.type.Location;

@Getter
@SuperBuilder(toBuilder = true)
public final class CashWithdrawLog extends AuditLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final Location location;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), CashWithdrawalLogView.builder()
                .amount(amount));
    }
}
