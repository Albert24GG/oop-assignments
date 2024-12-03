package org.poo.bank.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.utils.Utils;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class BankAccount {
    @Getter
    private final String iban = generateIban();
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private String alias;
    private final Type type;
    private final String currency;
    @Getter
    private final UserAccount owner;
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private double balance = 0.0;
    @Getter
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
