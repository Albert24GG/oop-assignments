package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.interfaces.UserTransactionLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.CardPaymentLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLog extends AuditLog implements UserTransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final String merchant;
    @NonNull
    // The user account that made the payment
    private final UserAccount userAccount;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), CardPaymentLogView.builder()
                .amount(amount)
                .merchant(merchant));
    }

    @Override
    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    public double getAmount() {
        return amount;
    }
}
