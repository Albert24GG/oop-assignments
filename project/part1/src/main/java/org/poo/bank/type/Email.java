package org.poo.bank.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public final class Email {
    private final String value;

    /**
     * Constructs an Email object.
     *
     * @param value the email address
     * @throws IllegalArgumentException if the email address is invalid
     */
    public Email(@NonNull final String value) {
        if (!value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.value = value;
    }

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
