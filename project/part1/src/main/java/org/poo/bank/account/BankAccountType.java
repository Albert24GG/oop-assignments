package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BankAccountType {
    SAVINGS, CLASSIC;

    /**
     * Gets the bank account type from a string
     *
     * @param type the string to convert
     * @return the bank account type or null if the string is not a valid bank account type
     */
    public static BankAccountType of(final String type) {
        return BankAccountType.valueOf(type.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
