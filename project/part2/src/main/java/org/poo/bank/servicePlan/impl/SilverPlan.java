package org.poo.bank.servicePlan.impl;

import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.Map;
import java.util.Optional;

public final class SilverPlan implements ServicePlan {
    private static final double COMMISSION = 0.001;
    private static final double COMMISSION_THRESHOLD = 500.0;
    private static final Map<ServicePlanType, Integer> UPGRADE_FEES = Map.of(
            ServicePlanType.GOLD, 250
    );
    private static final Map<ServicePlanType, ServicePlan> UPGRADE_OPTIONS = Map.of(
            ServicePlanType.GOLD, GoldPlan.getInstance()
    );
    private static SilverPlan instance = null;

    private SilverPlan() {
    }

    /**
     * Get the singleton instance of the silver plan
     *
     * @return the silver plan instance
     */
    public static SilverPlan getInstance() {
        if (instance == null) {
            instance = new SilverPlan();
        }
        return instance;
    }

    @Override
    public double getTransactionCommission(final double transactionAmount) {
        return transactionAmount >= COMMISSION_THRESHOLD ? COMMISSION : 0.0;
    }

    @Override
    public Optional<Integer> getUpgradeFee(final ServicePlanType newPlan) {
        return Optional.ofNullable(UPGRADE_FEES.get(newPlan));
    }

    @Override
    public ServicePlanType getServicePlanType() {
        return ServicePlanType.SILVER;
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
