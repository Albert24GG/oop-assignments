package org.poo.bank.report.business;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BusinessReportType {
    TRANSACTION,
    COMMERCIANT;

    /**
     * Returns the BusinessReportType that corresponds to the given type.
     *
     * @param type the type of the report
     * @return the BusinessReportType that corresponds to the given type
     * @throws IllegalArgumentException if the given type is invalid
     */
    public static BusinessReportType of(final String type) {
        try {
            return BusinessReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report type: " + type);
        }
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
