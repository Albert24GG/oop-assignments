package org.poo.bank.transaction.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.type.Currency;

@Getter
@SuperBuilder(toBuilder = true)
public final class InterestIncomeLogView extends TransactionLogView {
    private final double amount;
    private final Currency currency;
}
