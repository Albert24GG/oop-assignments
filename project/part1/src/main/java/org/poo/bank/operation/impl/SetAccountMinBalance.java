package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.ValidationUtil;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class SetAccountMinBalance extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    private final double minBalance;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount account;
        try {
            account = ValidationUtil.getBankAccountByIban(context.bankAccService(), accountIban);
        } catch (IllegalArgumentException e) {
            throw new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND, e.getMessage());
        }
        context.bankAccService().setMinBalance(account, minBalance);
        return BankOperationResult.success();
    }
}
