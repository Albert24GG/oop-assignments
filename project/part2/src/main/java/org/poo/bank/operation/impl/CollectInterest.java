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
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.InterestIncomeLog;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class CollectInterest extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() != BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This is not a savings account");
        }

        double collectedInterest = bankAccount.getBalance();

        context.bankAccService().collectInterest(bankAccount);

        collectedInterest = bankAccount.getBalance() - collectedInterest;

        TransactionLog transactionLog = InterestIncomeLog.builder()
                .timestamp(timestamp)
                .description("Interest rate income")
                .amount(collectedInterest)
                .currency(bankAccount.getCurrency())
                .build();

        BankOperationUtils.logTransaction(context, accountIban, transactionLog);

        return BankOperationResult.success();
    }
}
