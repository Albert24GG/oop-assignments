package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public enum BankAccountType {
    SAVINGS,
    CLASSIC,
    BUSINESS;

    /**
     * Gets the bank account type from a string
     *
     * @param type the string to convert
     * @return the bank account type or null if the string is not a valid bank account type
     */
    public static BankAccountType of(@NonNull final String type) {
        return BankAccountType.valueOf(type.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
