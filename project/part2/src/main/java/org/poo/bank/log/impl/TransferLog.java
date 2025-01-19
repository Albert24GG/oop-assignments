package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.interfaces.TransactionLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.log.view.impl.TransferLogView;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.Optional;


@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLog extends AuditLog implements TransactionLog {
    @NonNull
    private final IBAN senderIBAN;
    /**
     * The user that initiated the transfer
     */
    @NonNull
    private final UserAccount initiatingUser;
    @NonNull
    private final IBAN receiverIBAN;
    @NonNull
    private final Double amount;
    @NonNull
    private final Currency currency;
    @NonNull
    private final TransferType transferType;
    /*
     * The merchant that received the transfer
     * If the transfer was made to a user, this field is null
     */
    private final Merchant merchant;

    public enum TransferType {
        SENT,
        RECEIVED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public AuditLogView toView() {
        return AuditLogView.fromBase(super.toView(), TransferLogView.builder()
                .senderIBAN(senderIBAN)
                .receiverIBAN(receiverIBAN)
                .amount(amount)
                .currency(currency)
                .transferType(transferType.toString()));
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public Optional<UserAccount> getSenderUserAccount() {
        return Optional.of(initiatingUser);
    }

    @Override
    public Optional<Merchant> getRecipientMerchant() {
        return Optional.ofNullable(merchant);
    }
}
