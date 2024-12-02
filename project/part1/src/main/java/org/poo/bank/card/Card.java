package org.poo.bank.card;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.utils.Utils;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class Card {
    @Getter
    private final BankAccount linkedAccount;
    private final Type type;
    @Getter
    private final String number = generateNumber();
    private boolean frozen = false;

    public enum Type {
        DEBIT, SINGLE_USE;
    }

    public static Card createCard(Type type, BankAccount account) {
        return switch (type) {
            case DEBIT -> new DebitCard(account);
            case SINGLE_USE -> new SingleUseCard(account);
            case null -> null;
        };
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

}
