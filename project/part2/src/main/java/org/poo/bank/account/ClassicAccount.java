package org.poo.bank.account;

import org.poo.bank.type.Currency;

public final class ClassicAccount extends BankAccount {
    public ClassicAccount(final UserAccount owner, final Currency currency) {
        super(currency, owner);
    }

    @Override
    public BankAccountType getType() {
        return BankAccountType.CLASSIC;
    }
}
