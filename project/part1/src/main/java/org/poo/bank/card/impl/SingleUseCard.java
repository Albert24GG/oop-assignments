package org.poo.bank.card.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.card.Card;

public class SingleUseCard extends Card {
    public SingleUseCard(final BankAccount account) {
        super(account, Type.SINGLE_USE);
    }

    @Override
    protected void paymentMade() {
        // Regenerate the card number
        setNumber(generateNumber());
    }
}
