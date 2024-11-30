package org.poo.bank.account;

public class SavingsBankAcc extends BankAccount {
    private double interestRate;

    public SavingsBankAcc(final UserAccount owner, final String currency, double interestRate) {
        super(BankAccount.Type.SAVINGS, currency, owner);
        this.interestRate = interestRate;
    }

}
