package org.poo.bank.account;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.bank.card.Card;
import org.poo.utils.Utils;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter(AccessLevel.PACKAGE)
public abstract class BankAccount {
    @Getter(AccessLevel.PUBLIC)
    private final String iban = generateIban();
    @Setter(AccessLevel.PACKAGE)
    private String alias;
    private final Type type;
    private final String currency;
    @Getter(AccessLevel.PUBLIC)
    private final UserAccount owner;
    private final Set<Card> cards = new HashSet<>();
    @Setter(AccessLevel.PROTECTED)
    private double balance = 0.0;
    private double minBalance = 0.0;

    public enum Type {
        SAVINGS, CLASSIC;

        public Type fromString(final String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

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
    public static BankAccount createAccount(final Type type, @NonNull final UserAccount owner,
                                            final String currency,
                                            final double interestRate) {
        return switch (type) {
            case SAVINGS -> new SavingsBankAcc(owner, currency, interestRate);
            case CLASSIC -> new ClassicBankAcc(owner, currency);
            case null -> null;
        };
    }

    /**
     * Generates a new IBAN
     *
     * @return the generated IBAN
     */
    public static String generateIban() {
        return Utils.generateIBAN();
    }

    /**
     * Adds a card to the account
     *
     * @param card the card to add
     * @throws IllegalArgumentException if the card is not linked to this account
     */
    public void addCard(final Card card) {
        if (card.getLinkedAccount() != this) {
            throw new IllegalArgumentException("Card must be linked to this account");
        }

        cards.add(card);
    }

    void addFunds(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance += amount;
    }

    void removeFunds(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance - amount < minBalance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
    }

    void setMinBalance(final double minBalance) {
        if (minBalance < 0) {
            throw new IllegalArgumentException("Minimum balance must be positive");
        }
        this.minBalance = minBalance;
    }

    abstract void changeInterestRate(final double interestRate);

    abstract void collectInterest();
}
