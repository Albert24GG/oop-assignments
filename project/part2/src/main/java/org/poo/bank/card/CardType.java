package org.poo.bank.card;

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

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

