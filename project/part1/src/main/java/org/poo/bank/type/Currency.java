package org.poo.bank.type;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public record Currency(@NonNull String name) {
    /**
     * Constructs a Currency object.
     *
     * @param name the currency name
     */
    public Currency(@NonNull final String name) {
        this.name = name.toUpperCase();
    }

    /**
     * Creates a Currency object.
     *
     * @param name the currency name
     * @return the Currency object
     */
    public static Currency of(@NonNull final String name) {
        return new Currency(name);
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
