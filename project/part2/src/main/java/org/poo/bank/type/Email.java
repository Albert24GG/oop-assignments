package org.poo.bank.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public record Email(@NonNull String value) {
    /**
     * Creates an Email object.
     *
     * @param value the email address
     * @return the Email object
     */
    public static Email of(@NonNull final String value) {
        return new Email(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
