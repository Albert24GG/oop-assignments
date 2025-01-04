package org.poo.bank.type;

import com.fasterxml.jackson.annotation.JsonValue;

public record Location(String value) {
    /**
     * Creates a Location object.
     *
     * @param value the location
     * @return the Location object
     */
    public static Location of(final String value) {
        return new Location(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
