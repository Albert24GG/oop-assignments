package org.poo.bank.card;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.utils.Utils;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public abstract class Card {
    private final BankAccount linkedAccount;
    private final Type type;
    @Setter(lombok.AccessLevel.PROTECTED)
    private String number = generateNumber();
    private boolean frozen = false;

    public enum Type {
        DEBIT, SINGLE_USE;

        /**
         * Converts a string to a card type
         *
         * @param type the string to convert
         * @return the card type or null if the string is not a valid card type
         */
        public static Type fromString(String type) {
            try {
                return Type.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static Card createCard(Type type, BankAccount account) {
        Card card = switch (type) {
            case DEBIT -> new DebitCard(account);
            case SINGLE_USE -> new SingleUseCard(account);
            case null -> null;
        };
        if (card != null) {
            account.addCard(card);
        }
        return card;
    }

    /**
     * Gets the owner of the card
     *
     * @return the owner of the card
     */
    public UserAccount getOwner() {
        return linkedAccount.getOwner();
    }

    /**
     * Generates a new card number
     *
     * @return the generated card number
     */
    public static String generateNumber() {
        return Utils.generateCardNumber();
    }

    /**
     * Acknowledges that a payment has been made
     */
    abstract void paymentMade();
}
