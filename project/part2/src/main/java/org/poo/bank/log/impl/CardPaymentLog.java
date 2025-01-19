package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.interfaces.TransactionLog;
import org.poo.bank.log.interfaces.UserTransactionLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.CardPaymentLogView;
import org.poo.bank.merchant.Merchant;

import java.util.Optional;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLog extends AuditLog implements UserTransactionLog, TransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final Merchant merchant;
    @NonNull
    // The user account that made the payment
    private final UserAccount userAccount;

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), CardPaymentLogView.builder()
                .amount(amount)
                .merchant(merchant.getName()));
    }

    @Override
    public Optional<UserAccount> getSenderUserAccount() {
        return Optional.of(userAccount);
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public Optional<Merchant> getRecipientMerchant() {
        return Optional.of(merchant);
    }
}
