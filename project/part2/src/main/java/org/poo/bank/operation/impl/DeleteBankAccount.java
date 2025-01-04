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
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.AccountOpLog;
import org.poo.bank.transaction.impl.FailedOpLog;
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
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);

        if (!context.bankAccService().canDeleteAccount(bankAccount)) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description("Account couldn't be deleted - there are funds remaining")
                    .build();
            BankOperationUtils.logTransaction(context, bankAccount, transactionLog);

            throw new BankOperationException(BankErrorType.ACCOUNT_DELETION_FAILED);
        }

        TransactionLog transactionLog = AccountOpLog.builder()
                .timestamp(timestamp)
                .description("Account deleted")
                .build();
        BankOperationUtils.logTransaction(context, bankAccount, transactionLog);

        // Remove the account and its cards
        context.bankAccService().removeAccount(bankAccount);
        bankAccount.getCards().forEach(context.cardService()::removeCard);
        return BankOperationResult.success();
    }
}
