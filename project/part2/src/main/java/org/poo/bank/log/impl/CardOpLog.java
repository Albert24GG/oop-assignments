package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.CardOpLogView;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardOpLog extends AuditLog {
    @NonNull
    private final CardNumber card;
    @NonNull
    private final Email cardHolder;
    @NonNull
    private final IBAN account;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), CardOpLogView.builder()
                .card(card)
                .cardHolder(cardHolder)
                .account(account));
    }
}
