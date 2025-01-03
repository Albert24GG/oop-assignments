package org.poo.bank.servicePlan.impl;

import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.Optional;

public final class GoldPlan implements ServicePlan {
    private static final double COMMISSION = 0.0;

    @Override
    public double getTransactionCommission(final double transactionAmount) {
        return COMMISSION;
    }

    @Override
    public Optional<Integer> getUpgradeFee(final ServicePlanType newPlan) {
        return Optional.empty();
    }

    @Override
    public ServicePlanType getServicePlanType() {
        return ServicePlanType.GOLD;
    }

    @Override
    public ServicePlan upgradePlan(final ServicePlanType newPlan) {
        if (getServicePlanType() == newPlan) {
            return this;
        }

        throw new IllegalArgumentException("Invalid service plan upgrade");
    }
}
