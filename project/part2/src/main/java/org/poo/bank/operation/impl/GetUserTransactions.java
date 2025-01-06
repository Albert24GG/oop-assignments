package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.type.Email;

import java.util.Comparator;
import java.util.List;

@Builder
@RequiredArgsConstructor
public final class GetUserTransactions extends BankOperation<List<TransactionLogView>> {
    @NonNull
    private final Email userEmail;

    @Override
    protected BankOperationResult<List<TransactionLogView>> internalExecute(
            final BankOperationContext context) throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);

        List<TransactionLogView> transactionViews = userAccount.getAccounts().stream()
                .flatMap(
                        account -> context.transactionLogService().getLogs(account.getIban())
                                .stream())
                .sorted(Comparator.comparing(TransactionLog::getTimestamp))
                .map(TransactionLog::toView)
                .toList();
        return BankOperationResult.success(transactionViews);
    }
}
