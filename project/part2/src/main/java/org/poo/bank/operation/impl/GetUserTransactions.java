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
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.type.Email;

import java.util.Comparator;
import java.util.List;

@Builder
@RequiredArgsConstructor
public final class GetUserTransactions extends BankOperation<List<AuditLogView>> {
    @NonNull
    private final Email userEmail;

    @Override
    protected BankOperationResult<List<AuditLogView>> internalExecute(
            final BankOperationContext context) throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);

        List<AuditLogView> transactionViews = userAccount.getAccounts().stream()
                .flatMap(
                        account -> context.auditLogService().getLogs(account.getIban())
                                .stream())
                .sorted(Comparator.comparing(AuditLog::getTimestamp))
                .map(AuditLog::toView)
                .toList();
        return BankOperationResult.success(transactionViews);
    }
}
