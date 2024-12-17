package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class CollectInterest extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount account = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));

        if (account.getType() != BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This is not a savings account");
        }

        context.bankAccService().collectInterest(account);
        return BankOperationResult.success();
    }
}
