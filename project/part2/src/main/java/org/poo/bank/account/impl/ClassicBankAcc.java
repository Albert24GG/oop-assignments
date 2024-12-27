package org.poo.bank.account.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.UserAccount;
import org.poo.bank.type.Currency;

public final class ClassicBankAcc extends BankAccount {
    public ClassicBankAcc(final UserAccount owner, final Currency currency) {
        super(BankAccountType.CLASSIC, currency, owner);
    }

    @Override
    protected void changeInterestRate(final double interestRate) {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }

    @Override
    protected void collectInterest() {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }
}
