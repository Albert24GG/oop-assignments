package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.type.Email;

import java.util.Comparator;
import java.util.List;

@Builder
@RequiredArgsConstructor
public final class GetUserTransactions extends BankOperation<List<TransactionLog>> {
    @NonNull
    private final Email userEmail;

    @Override
    protected BankOperationResult<List<TransactionLog>> internalExecute(
            final BankOperationContext context) throws BankOperationException {
        UserAccount user = context.userService().getUser(userEmail).orElseThrow(
                () -> new BankOperationException(BankErrorType.USER_NOT_FOUND));

        List<TransactionLog> transactions = user.getAccounts().stream()
                .flatMap(
                        account -> context.transactionService().getLogs(account.getIban()).stream())
                .sorted(Comparator.comparing(TransactionLog::getTimestamp))
                .toList();
        return BankOperationResult.success(transactions);
    }
}
