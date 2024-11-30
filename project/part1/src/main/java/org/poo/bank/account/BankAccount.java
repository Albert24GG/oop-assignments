package org.poo.bank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.utils.Utils;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class BankAccount {
    @Getter
    private final Iban iban = Iban.generate();
    private final Type type;
    private final String currency;
    @Getter
    private final UserAccount owner;
    @Setter
    private double balance = 0.0;
    @Setter
    private double minBalance = 0.0;

    public enum Type {
        SAVINGS, CLASSIC;

        public Type fromString(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public record Iban(String value) {
        public static Iban generate() {
            return new Iban(Utils.generateIBAN());
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
    public static BankAccount createAccount(Type type, UserAccount owner, String currency,
                                            double interestRate) {
        return switch (type) {
            case SAVINGS -> new SavingsBankAcc(owner, currency, interestRate);
            case CLASSIC -> new ClassicBankAcc(owner, currency);
            case null -> null;
        };
    }
}
