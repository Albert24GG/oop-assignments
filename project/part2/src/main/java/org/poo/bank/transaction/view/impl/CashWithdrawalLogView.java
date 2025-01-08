package org.poo.bank.transaction.view.impl;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.view.TransactionLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CashWithdrawalLogView extends TransactionLogView {
    private final double amount;
}
