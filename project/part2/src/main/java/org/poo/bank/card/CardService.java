package org.poo.bank.card;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.type.CardNumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CardService {
    /**
     * Mapping between the card number and the card.
     */
    private final Map<CardNumber, Card> cards = new HashMap<>();

    /**
     * Create a new card.
     *
     * @param account the bank account of the card
     * @param type    the type of the card
     * @param creator the user that is creating the card
     * @return the created card
     */
    public Card createCard(final BankAccount account, final CardType type,
                           final UserAccount creator) {
        Card card = Card.createCard(type, account, creator);
        cards.put(card.getNumber(), card);
        return card;
    }

    /**
     * Get the card with the given number.
     *
     * @param number the number of the card
     * @return an {@link Optional} containing the card with the given number, or an
     * {@link Optional#empty()} if the card does not exist
     */
    public Optional<Card> getCard(final CardNumber number) {
        return Optional.ofNullable(cards.get(number));
    }

    /**
     * Remove the given card.
     *
     * @param card the card to remove
     * @return an {@link Optional} containing the removed card, or an
     * {@link Optional#empty()} if the card does not exist
     */
    public Optional<Card> removeCard(final Card card) {
        card.getLinkedAccount().removeCard(card);
        return Optional.ofNullable(cards.remove(card.getNumber()));
    }

    /**
     * Validate that the card is owned by the given user.
     *
     * @param card the card
     * @param user the user
     * @return {@code true} if the card is owned by the user, {@code false} otherwise
     */
    public boolean validateCardOwnership(final Card card, final UserAccount user) {
        return card.getOwner().equals(user);
    }

    /**
     * Update the status of the card.
     * The status is updated based on the balance of the linked account.
     *
     * @param card the card
     * @return the new status of the card
     */
    public Card.Status updateCardStatus(final Card card) {
        return card.updateStatus();
    }
}
