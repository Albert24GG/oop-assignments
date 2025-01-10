package org.poo.bank.account;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.bank.card.Card;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public abstract class BankAccount {
    private final IBAN iban = IBAN.generate();
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private String alias;
    private final BankAccountType type;
    private final Currency currency;
    private final UserAccount owner;
    @EqualsAndHashCode.Exclude
    private final Set<Card> cards = new LinkedHashSet<>();
    @Setter(AccessLevel.PROTECTED)
    private double balance = 0.0;
    private double minBalance = 0.0;


    /**
     * Creates a new bank account
     *
     * @param type         the type of the account
     * @param owner        the owner of the account
     * @param currency     the currency of the account
     * @param interestRate the interest rate of the account. This parameter is only used for savings
     *                     accounts
     * @return the new bank account
     */
    static BankAccount createAccount(final BankAccountType type, @NonNull final UserAccount owner,
                                     @NonNull final Currency currency,
                                     final double interestRate) {
        BankAccount newAccount = switch (type) {
            case SAVINGS -> new SavingsAccount(owner, currency, interestRate);
            case CLASSIC -> new ClassicAccount(owner, currency);
            case null -> null;
        };

        if (newAccount != null) {
            owner.addAccount(newAccount);
        }
        return newAccount;
    }

    /**
     * Adds a card to the account
     *
     * @param card the card to add
     * @throws IllegalArgumentException if the card is not linked to this account
     */
    public final void addCard(@NonNull final Card card) {
        if (card.getLinkedAccount() != this) {
            throw new IllegalArgumentException("Card must be linked to this account");
        }

        cards.add(card);
    }

    /**
     * Removes a card from the account
     *
     * @param card the card to remove
     * @return an {@link Optional} containing the removed card, or an
     * {@link Optional#empty()} if the card does not exist
     */
    public final Optional<Card> removeCard(@NonNull final Card card) {
        return Optional.ofNullable(cards.remove(card) ? card : null);
    }

    final void addFunds(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance += amount;
    }

    final void removeFunds(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance - amount < minBalance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
    }

    final void setMinBalance(final double minBalance) {
        if (minBalance < 0) {
            throw new IllegalArgumentException("Minimum balance must be positive");
        }
        this.minBalance = minBalance;
    }
}
