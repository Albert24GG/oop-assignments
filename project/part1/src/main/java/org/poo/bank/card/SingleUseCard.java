package org.poo.bank.card;

import org.poo.bank.account.BankAccount;

public class SingleUseCard extends Card {
    public SingleUseCard(final BankAccount account) {
        super(account, Type.SINGLE_USE);
    }
}
