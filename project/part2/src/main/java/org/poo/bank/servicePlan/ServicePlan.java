package org.poo.bank.servicePlan;

import java.util.Optional;

public interface ServicePlan {
    /**
     * Get the transaction commission for the current service plan
     *
     * @param transactionAmount the transaction amount in RON
     * @return the transaction commission as a percentage
     */
    double getTransactionCommission(double transactionAmount);

    /**
     * Get the cost to upgrade to a new service plan
     *
     * @param newPlan the new service plan
     * @return an {@link Optional} containing the upgrade fee if the current plan can be upgraded
     * to the new plan, or an {@link Optional#empty()} otherwise
     */
    Optional<Integer> getUpgradeFee(ServicePlanType newPlan);

    /**
     * Get the service plan type
     *
     * @return the service plan type
     */
    ServicePlanType getServicePlanType();

    /**
     * Upgrade the service plan to a new plan
     *
     * @param newPlan the new service plan
     * @return the upgraded service plan
     */
    ServicePlan upgradePlan(ServicePlanType newPlan);
}
