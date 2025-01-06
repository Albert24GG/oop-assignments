package org.poo.bank.transaction.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.servicePlan.ServicePlanType;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class UpgradePlanLogView extends TransactionLogView {
    private final IBAN accountIBAN;
    private final ServicePlanType newPlanType;
}
