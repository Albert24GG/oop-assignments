package org.poo.bank.log.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.type.Currency;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestClaimLogView extends AuditLogView {
    private final double amount;
    private final Currency currency;
}
