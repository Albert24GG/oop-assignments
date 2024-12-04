package org.poo.bank.card;

import lombok.NonNull;
import org.poo.bank.account.BankAccount;

import java.util.HashMap;
import java.util.Map;

public final class CardService {
    /**
     * Mapping between the card number and the card.
     */
    private final Map<String, Card> cards = new HashMap<>();

    /**
     * Create a new card.
     *
     * @param account the bank account of the card
     * @param type    the type of the card
     * @return the created card
     * @throws IllegalArgumentException if the card already exists
     */
    public Card createCard(@NonNull final BankAccount account, final Card.Type type)
            throws IllegalArgumentException {
        if (cards.containsKey(account.getIban())) {
            throw new IllegalArgumentException("Card already exists");
        }

        Card card = Card.createCard(type, account);
        cards.put(card.getNumber(), card);
        return card;
    }

    /**
     * Get the card with the given number.
     *
     * @param number the number of the card
     * @return the card with the given number, or {@code null} if the card does not exist
     */
    public Card getCard(final String number) {
        return cards.get(number);
    }

    /**
     * Remove the given card.
     *
     * @param card the card to remove
     * @return the removed card, or {@code null} if the card does not exist
     */
    public Card removeCard(@NonNull final Card card) {
        return cards.remove(card.getNumber());
    }
}
