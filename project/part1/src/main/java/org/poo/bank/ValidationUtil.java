package org.poo.bank;

import lombok.NonNull;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.account.UserService;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardService;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

import java.util.Optional;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    /**
     * Gets a bank account from the bank account service by IBAN
     *
     * @param bankAccService the bank account service
     * @param iban           the IBAN of the account
     * @return the bank account
     * @throws IllegalArgumentException if the account is not found
     */
    public static BankAccount getBankAccountByIban(final BankAccService bankAccService,
                                                   final IBAN iban) {
        return Optional.ofNullable(bankAccService.getAccountByIban(iban))
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Gets a bank account from the bank account service by alias
     *
     * @param bankAccService the bank account service
     * @param alias          the alias of the account
     * @return the bank account
     * @throws IllegalArgumentException if the account is not found
     */
    public static BankAccount getBankAccountByAlias(final BankAccService bankAccService,
                                                    final String alias) {
        return Optional.ofNullable(bankAccService.getAccountByAlias(alias))
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
    public static UserAccount getUserAccount(final UserService userService,
                                             @NonNull final Email email) {
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
    public static Card getCard(final CardService cardService,
                               @NonNull final CardNumber cardNumber) {
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
}
