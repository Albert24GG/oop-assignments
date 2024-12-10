package org.poo.bank.account.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;

public class ClassicBankAcc extends BankAccount {

    public ClassicBankAcc(final UserAccount owner, final String currency) {
        super(BankAccount.Type.CLASSIC, currency, owner);
    }

    @Override
    protected void changeInterestRate(double interestRate) {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }

    @Override
    protected void collectInterest() {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }
}
