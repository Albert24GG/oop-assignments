package org.poo.bank.servicePlan;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public enum ServicePlanType {
    STANDARD,
    STUDENT,
    SILVER,
    GOLD;

    /**
     * Get the service plan type from a string
     *
     * @param type the type of the service plan
     * @return the service plan type
     */
    public static ServicePlanType of(@NonNull final String type) {
        try {
            return ServicePlanType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service plan type: " + type);
        }
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
