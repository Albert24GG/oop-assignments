package org.poo.bank.account;

public class ClassicBankAcc extends BankAccount {

    public ClassicBankAcc(final UserAccount owner, final String currency) {
        super(BankAccount.Type.CLASSIC, currency, owner);
    }

}
