package org.poo.bank.log.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.interfaces.UserTransactionLog;

@Getter
@SuperBuilder(toBuilder = true)
public final class DepositLog extends AuditLog implements UserTransactionLog {
    @NonNull
    private final Double amount;
    @NonNull
    private final BankAccount bankAccount;
    @NonNull
    private final UserAccount userAccount;

    @Override
    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    public double getAmount() {
        return amount;
    }
}
