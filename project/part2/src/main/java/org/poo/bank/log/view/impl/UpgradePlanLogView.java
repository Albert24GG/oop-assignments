package org.poo.bank.log.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.servicePlan.ServicePlanType;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class UpgradePlanLogView extends AuditLogView {
    private final IBAN accountIBAN;
    private final ServicePlanType newPlanType;
}
