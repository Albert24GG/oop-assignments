package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.GenericLog;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class DeleteBankAccount extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final IBAN accountIban;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        UserAccount userAccount = context.userService().getUser(ownerEmail)
                .orElseThrow(() -> new BankOperationException(BankErrorType.USER_NOT_FOUND));
        BankAccount bankAccount = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));

        if (!context.bankAccService().validateAccountOwnership(bankAccount, userAccount)) {
            throw new BankOperationException(BankErrorType.USER_NOT_ACCOUNT_OWNER);
        }

        if (bankAccount.getBalance() != 0) {
            TransactionLog transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .description("Account couldn't be deleted - there are funds remaining")
                    .build();
            context.transactionService().logTransaction(accountIban, transactionLog);
            throw new BankOperationException(BankErrorType.ACCOUNT_DELETION_FAILED);
        }

        TransactionLog transactionLog = GenericLog.builder()
                .timestamp(timestamp)
                .description("Account deleted")
                .build();
        context.transactionService().logTransaction(accountIban, transactionLog);
        // Remove the account and its cards
        context.bankAccService().removeAccount(bankAccount);
        bankAccount.getCards().forEach(context.cardService()::removeCard);
        return BankOperationResult.success();
    }
}
