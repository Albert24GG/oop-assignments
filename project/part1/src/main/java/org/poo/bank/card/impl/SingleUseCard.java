package org.poo.bank.card.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;
import org.poo.bank.type.CardNumber;

public final class SingleUseCard extends Card {
    public SingleUseCard(final BankAccount account) {
        super(account, CardType.SINGLE_USE);
    }

    @Override
    protected void paymentMade() {
        // Regenerate the card number
        setNumber(CardNumber.generate());
    }
}
