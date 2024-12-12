package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.ValidationUtil;
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
        UserAccount userAccount;
        BankAccount bankAccount;
        try {
            userAccount = ValidationUtil.getUserAccount(context.userService(), ownerEmail);
            bankAccount =
                    ValidationUtil.getBankAccountByIban(context.bankAccService(), accountIban);
            ValidationUtil.validateAccountOwnership(bankAccount, userAccount);
        } catch (IllegalArgumentException e) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT, e.getMessage());
        }

        if (bankAccount.getBalance() != 0) {
            TransactionLog transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .error("Account couldn't be deleted - there are funds remaining")
                    .build();
            context.transactionService().logTransaction(accountIban, transactionLog);
            throw new IllegalArgumentException(
                    "Account couldn't be deleted - see org.poo.transactions for details");
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
