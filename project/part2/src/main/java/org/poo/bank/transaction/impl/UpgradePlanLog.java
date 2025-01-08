package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.transaction.view.impl.UpgradePlanLogView;
import org.poo.bank.type.IBAN;
import org.poo.bank.servicePlan.ServicePlanType;

@SuperBuilder(toBuilder = true)
public final class UpgradePlanLog extends AuditLog {
    @NonNull
    private final IBAN accountIBAN;
    @NonNull
    private final ServicePlanType newPlanType;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), UpgradePlanLogView.builder()
                .accountIBAN(accountIBAN)
                .newPlanType(newPlanType));
    }
}
