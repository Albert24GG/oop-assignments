package org.poo.bank.card;

import org.poo.bank.account.BankAccount;

public class DebitCard extends Card {
    public DebitCard(final BankAccount account) {
        super(account, Type.DEBIT);
    }

    @Override
    void paymentMade() {
        // Do nothing
    }
}
