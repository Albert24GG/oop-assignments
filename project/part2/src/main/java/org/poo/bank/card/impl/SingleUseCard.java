package org.poo.bank.card.impl;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;

public final class SingleUseCard extends Card {
    public SingleUseCard(final BankAccount account, final UserAccount creator) {
        super(account, creator, CardType.SINGLE_USE);
    }
}
