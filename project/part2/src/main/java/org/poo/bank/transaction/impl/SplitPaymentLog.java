package org.poo.bank.transaction.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.splitPayment.SplitPaymentType;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.transaction.view.impl.SplitPaymentLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public final class SplitPaymentLog extends AuditLog {
    @NonNull
    private final Currency currency;
    private final Double amount;
    private final List<Double> amountPerAccount;
    @NonNull
    private final List<IBAN> involvedAccounts;
    @NonNull
    private final SplitPaymentType type;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), SplitPaymentLogView.builder()
                .currency(currency)
                .amount(amount)
                .involvedAccounts(List.copyOf(involvedAccounts))
                .amountPerAccount(amountPerAccount == null ? null : List.copyOf(amountPerAccount))
                .splitPaymentType(type));
    }
}
