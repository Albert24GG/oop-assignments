package org.poo.bank.servicePlan.impl;

import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.Map;
import java.util.Optional;

public final class StudentPlan implements ServicePlan {
    private static final double COMMISSION = 0.0;
    private static final Map<ServicePlanType, Integer> UPGRADE_FEES = Map.of(
            ServicePlanType.SILVER, 100,
            ServicePlanType.GOLD, 350
    );
    private static final Map<ServicePlanType, ServicePlan> UPGRADE_OPTIONS = Map.of(
            ServicePlanType.SILVER, SilverPlan.getInstance(),
            ServicePlanType.GOLD, GoldPlan.getInstance()
    );
    private static StudentPlan instance = null;

    private StudentPlan() {
    }

    /**
     * Get the singleton instance of the student plan
     *
     * @return the student plan instance
     */
    public static StudentPlan getInstance() {
        if (instance == null) {
            instance = new StudentPlan();
        }
        return instance;
    }

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
        return ServicePlanType.STUDENT;
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
