package org.poo.bank.account;

import org.poo.bank.type.Currency;

public final class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(final UserAccount owner, final Currency currency,
                          final double interestRate) {
        super(BankAccountType.SAVINGS, currency, owner);
        this.interestRate = interestRate;
    }

    void changeInterestRate(final double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate must be positive");
        }
        this.interestRate = interestRate;
    }

    void collectInterest() {
        setBalance(getBalance() * (1 + interestRate));
    }
}
