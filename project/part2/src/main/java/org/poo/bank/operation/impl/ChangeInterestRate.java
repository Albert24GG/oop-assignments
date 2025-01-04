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
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() != BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                    "This is not a savings account");
        }

        TransactionLog transactionLog = InterestOpLog.builder()
                .timestamp(timestamp)
                .description("Interest rate of the account changed to " + newInterestRate)
                .build();
        BankOperationUtils.logTransaction(context, bankAccount, transactionLog);

        context.bankAccService().changeInterestRate(bankAccount, newInterestRate);
        return BankOperationResult.success();
    }
}
