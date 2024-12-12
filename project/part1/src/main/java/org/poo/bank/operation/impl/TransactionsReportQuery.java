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
import org.poo.bank.report.TransactionsReport;
import org.poo.bank.transaction.TransactionLog;
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
    protected BankOperationResult<TransactionsReport> internalExecute(BankOperationContext context)
            throws BankOperationException {
        BankAccount account = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));

        List<TransactionLog>
                transactions =
                context.transactionService().getLogs(accountIban, startTimestamp, endTimestamp);

        // For savings accounts, only interest operations are shown
        if (account.getType() == BankAccountType.SAVINGS) {
            transactions = transactions.stream()
                    .filter(transactionLog -> transactionLog.getType() ==
                            TransactionLog.Type.INTEREST_OPERATION).toList();
        }
        return BankOperationResult.success(TransactionsReport.builder()
                .iban(accountIban)
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .transactions(transactions)
                .build());
    }
}
