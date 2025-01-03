package org.poo.bank.servicePlan.impl;

import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.Map;
import java.util.Optional;

public final class StandardPlan implements ServicePlan {
    private static final double COMMISSION = 0.02;
    private static final Map<ServicePlanType, Integer> UPGRADE_FEES = Map.of(
            ServicePlanType.SILVER, 100,
            ServicePlanType.GOLD, 350
    );
    private static final Map<ServicePlanType, ServicePlan> UPGRADE_OPTIONS = Map.of(
            ServicePlanType.SILVER, new SilverPlan(),
            ServicePlanType.GOLD, new GoldPlan()
    );

    @Override
    public double getTransactionCommission(final double transactionAmount) {
        return COMMISSION;
    }

    @Override
    public Optional<Integer> getUpgradeFee(final ServicePlanType newPlan) {
        return Optional.ofNullable(UPGRADE_FEES.get(newPlan));
    }

    @Override
    public ServicePlanType getServicePlanType() {
        return ServicePlanType.STANDARD;
    }

    @Override
    public ServicePlan upgradePlan(final ServicePlanType newPlan) {
        if (getServicePlanType() == newPlan) {
            return this;
        }

        ServicePlan newServicePlan = UPGRADE_OPTIONS.get(newPlan);
        if (newServicePlan == null) {
            throw new IllegalArgumentException("Invalid service plan upgrade");
        }

        return newServicePlan;
    }
}
