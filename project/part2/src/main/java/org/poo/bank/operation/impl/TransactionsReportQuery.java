package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.report.TransactionsReport;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogType;
import org.poo.bank.type.IBAN;

import java.util.List;

@Builder
@RequiredArgsConstructor
public final class TransactionsReportQuery extends BankOperation<TransactionsReport> {
    @NonNull
    private final IBAN accountIban;
    private final int startTimestamp;
    private final int endTimestamp;

    @Override
    protected BankOperationResult<TransactionsReport> internalExecute(
            final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        List<TransactionLog>
                transactions =
                context.transactionLogService().getLogs(accountIban, startTimestamp, endTimestamp);

        // For savings accounts, only interest operations are shown
        if (bankAccount.getType() == BankAccountType.SAVINGS) {
            transactions = transactions.stream()
                    .filter(transactionLog -> transactionLog.getType()
                            == TransactionLogType.INTEREST_CHANGE).toList();
        }

        return BankOperationResult.success(TransactionsReport.builder()
                .iban(accountIban)
                .balance(bankAccount.getBalance())
                .currency(bankAccount.getCurrency())
                .transactions(transactions.stream().map(TransactionLog::toView).toList())
                .build());
    }
}
