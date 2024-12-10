package org.poo.bank.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;
import org.poo.utils.Utils;

public record CardNumber(String value) {
    /**
     * Generates a random card number.
     *
     * @return the card number
     */
    public static CardNumber generate() {
        return new CardNumber(Utils.generateCardNumber());
    }

    /**
     * Constructs a CardNumber object.
     *
     * @param value the card number
     * @throws IllegalArgumentException if the card number is invalid
     */
    public static CardNumber of(@NonNull final String value) {
        return new CardNumber(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
