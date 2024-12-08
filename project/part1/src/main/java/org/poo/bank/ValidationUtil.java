package org.poo.bank;

import org.poo.bank.account.BankAccService;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.account.UserService;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardService;

import java.util.Optional;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    /**
     * Gets a bank account from the bank account service
     *
     * @param bankAccService the bank account service
     * @param accountIban    the account IBAN
     * @return the bank account
     * @throws IllegalArgumentException if the account is not found
     */
    public static BankAccount getBankAccount(final BankAccService bankAccService,
                                             final String accountIban) {
        return Optional.ofNullable(bankAccService.getAccount(accountIban))
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Gets a user account from the user service
     *
     * @param userService the user service
     * @param email       the user email
     * @return the user account
     * @throws IllegalArgumentException if the user is not found
     */
    public static UserAccount getUserAccount(final UserService userService, final String email) {
        return Optional.ofNullable(userService.getUser(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Gets a card from the card service
     *
     * @param cardService the card service
     * @param cardNumber  the card number
     * @return the card
     * @throws IllegalArgumentException if the card is not found
     */
    public static Card getCard(final CardService cardService, final String cardNumber) {
        return Optional.ofNullable(cardService.getCard(cardNumber))
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    /**
     * Validates that the user is the owner of the account
     *
     * @param account the account
     * @param user    the user
     * @throws IllegalArgumentException if the user is not the owner of the account
     */
    public static void validateAccountOwnership(final BankAccount account, final UserAccount user) {
        if (!account.getOwner().equals(user)) {
            throw new IllegalArgumentException("User is not the owner of the account");
        }
    }

    /**
     * Validates that the user is the owner of the card
     *
     * @param card the card
     * @param user the user
     * @throws IllegalArgumentException if the user is not the owner of the card
     */
    public static void validateCardOwnership(final Card card, final UserAccount user) {
        if (!card.getOwner().equals(user)) {
            throw new IllegalArgumentException("User is not the owner of the card");
        }
    }

    /**
     * Checks if the card is frozen
     *
     * @param card the card
     * @throws IllegalArgumentException if the card is frozen
     */
    public static void validateCardNotFrozen(final Card card) {
        if (card.isFrozen()) {
            throw new IllegalArgumentException("Card is frozen");
        }
    }

}
