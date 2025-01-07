package org.poo.bank.splitPayment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SplitPaymentType {
    EQUAL,
    CUSTOM;

    /**
     * Get the split payment type from the given string.
     *
     * @param type the string representation of the split payment type
     * @return the split payment type
     * @throws IllegalArgumentException if the given string is not a valid split payment type
     */
    public static SplitPaymentType of(final String type) {
        try {
            return SplitPaymentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid split payment type: " + type);
        }
    }

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
