package org.poo.bank.transaction.impl;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.transaction.view.impl.UpgradePlanLogView;
import org.poo.bank.type.IBAN;
import org.poo.bank.servicePlan.ServicePlanType;

@SuperBuilder(toBuilder = true)
public final class UpgradePlanLog extends TransactionLog {
    @NonNull
    private final IBAN accountIBAN;
    @NonNull
    private final ServicePlanType newPlanType;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.UPGRADE_PLAN;
    }

    @Override
    public TransactionLogView toView() {
        return UpgradePlanLogView.builder()
                .timestamp(getTimestamp())
                .description(getDescription())
                .error(getError())
                .type(getType())
                .accountIBAN(accountIBAN)
                .newPlanType(newPlanType)
                .build();
    }
}
