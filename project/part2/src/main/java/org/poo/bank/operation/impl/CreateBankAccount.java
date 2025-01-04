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
import org.poo.bank.transaction.impl.AccountOpLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;

@Builder
@RequiredArgsConstructor
public final class CreateBankAccount extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final Currency currency;
    @NonNull
    private final BankAccountType type;
    private final double interestRate;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount bankAccount = context.bankAccService()
                .createAccount(context.userService().getUser(ownerEmail)
                                .orElseThrow(() -> new BankOperationException(
                                        BankErrorType.USER_NOT_FOUND)), currency, type,
                        interestRate);

        TransactionLog transactionLog = AccountOpLog.builder()
                .timestamp(timestamp)
                .description("New account created")
                .build();
        BankOperationUtils.logTransaction(context, bankAccount, transactionLog);

        return BankOperationResult.success();
    }
}
