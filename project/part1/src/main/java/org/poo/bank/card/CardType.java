package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public enum CardType {
    DEBIT, SINGLE_USE;

    /**
     * Gets the card type from a string
     *
     * @param type the type of the card
     * @return the card type
     * @throws IllegalArgumentException if the type is not valid
     */
    public static CardType of(@NonNull final String type) {
        return CardType.valueOf(type.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

