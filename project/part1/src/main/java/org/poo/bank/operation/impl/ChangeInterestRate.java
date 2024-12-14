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
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.InterestOpLog;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class ChangeInterestRate extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    private final double newInterestRate;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(BankOperationContext context)
            throws BankOperationException {
        BankAccount account = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));

        if (account.getType() != BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                    "This is not a savings account");
        }

        TransactionLog transactionLog = InterestOpLog.builder()
                .timestamp(timestamp)
                .description("Interest rate of the account changed to " + newInterestRate)
                .build();
        context.transactionService().logTransaction(accountIban, transactionLog);

        context.bankAccService().changeInterestRate(account, newInterestRate);
        return BankOperationResult.success();
    }
}
