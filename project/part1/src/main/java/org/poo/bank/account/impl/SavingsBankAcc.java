package org.poo.bank.account.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;

public class SavingsBankAcc extends BankAccount {
    private double interestRate;

    public SavingsBankAcc(final UserAccount owner, final String currency, double interestRate) {
        super(BankAccount.Type.SAVINGS, currency, owner);
        this.interestRate = interestRate;
    }

    @Override
    protected void changeInterestRate(double interestRate) {
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
