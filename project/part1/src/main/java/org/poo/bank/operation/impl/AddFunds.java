package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class AddFunds extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount bankAccount = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(
                        () -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));
        context.bankAccService().addFunds(bankAccount, amount);
        return BankOperationResult.success();
    }
}
