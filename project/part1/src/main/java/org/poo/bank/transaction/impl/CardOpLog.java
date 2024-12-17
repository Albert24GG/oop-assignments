package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardOpLog extends TransactionLog {
    @NonNull
    private final CardNumber card;
    @NonNull
    private final Email cardHolder;
    @NonNull
    private final IBAN account;

    @Override
    public TransactionLogType getType() {
        return TransactionLogType.CARD_OPERATION;
    }

    @Override
    public TransactionLogView toView() {
        return super.toView().toBuilder()
                .card(card)
                .cardHolder(cardHolder)
                .account(account)
                .build();
    }
}
