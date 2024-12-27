package org.poo.bank.card.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;

public final class SingleUseCard extends Card {
    public SingleUseCard(final BankAccount account) {
        super(account, CardType.SINGLE_USE);
    }
}
