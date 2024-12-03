package org.poo.bank.account;

public class SavingsBankAcc extends BankAccount {
    private double interestRate;

    public SavingsBankAcc(final UserAccount owner, final String currency, double interestRate) {
        super(BankAccount.Type.SAVINGS, currency, owner);
        this.interestRate = interestRate;
    }

    @Override
    void changeInterestRate(double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate must be positive");
        }
        this.interestRate = interestRate;
    }

    @Override
    void collectInterest() {
        setBalance(getBalance() * (1 + interestRate));
    }
}
