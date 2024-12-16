package org.poo.bank.account.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.UserAccount;
import org.poo.bank.type.Currency;

public final class SavingsBankAcc extends BankAccount {
    private double interestRate;

    public SavingsBankAcc(final UserAccount owner, final Currency currency,
                          final double interestRate) {
        super(BankAccountType.SAVINGS, currency, owner);
        this.interestRate = interestRate;
    }

    @Override
    protected void changeInterestRate(final double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate must be positive");
        }
        this.interestRate = interestRate;
    }

    @Override
    protected void collectInterest() {
        setBalance(getBalance() * (1 + interestRate));
    }
}
