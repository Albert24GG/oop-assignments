package org.poo.bank.account;

public class ClassicBankAcc extends BankAccount {

    public ClassicBankAcc(final UserAccount owner, final String currency) {
        super(BankAccount.Type.CLASSIC, currency, owner);
    }

    @Override
    void changeInterestRate(double interestRate) {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }

    @Override
    void collectInterest() {
        throw new UnsupportedOperationException("Classic accounts do not have an interest rate");
    }
}
