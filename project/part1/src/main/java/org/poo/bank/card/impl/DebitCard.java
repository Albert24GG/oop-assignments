package org.poo.bank.card.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.card.Card;

public class DebitCard extends Card {
    public DebitCard(final BankAccount account) {
        super(account, Type.DEBIT);
    }

    @Override
    protected void paymentMade() {
        // Do nothing
    }
}
