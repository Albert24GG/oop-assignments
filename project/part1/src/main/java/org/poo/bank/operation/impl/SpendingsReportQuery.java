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
import org.poo.bank.report.MerchantSpending;
import org.poo.bank.report.SpendingsReport;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.CardPaymentLog;
import org.poo.bank.type.IBAN;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
public final class SpendingsReportQuery extends BankOperation<SpendingsReport> {
    @NonNull
    private final IBAN accountIban;
    private final int startTimestamp;
    private final int endTimestamp;

    @Override
    protected BankOperationResult<SpendingsReport> internalExecute(BankOperationContext context)
            throws BankOperationException {
        BankAccount account = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));

        if (account.getType() == BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This kind of report is not supported for a saving account");
        }

        // Get all card payments
        List<TransactionLog>
                transactions =
                context.transactionLogService()
                        .getLogs(accountIban, startTimestamp, endTimestamp)
                        .stream().filter(transactionLog -> transactionLog.getType() ==
                                TransactionLog.Type.CARD_PAYMENT)
                        .toList();

        // Group by merchant and sum the amounts to get the total spending per merchant
        List<MerchantSpending> merchants = transactions.stream()
                .map(CardPaymentLog.class::cast)
                .collect(
                        Collectors.groupingBy(CardPaymentLog::getMerchant,
                                Collectors.summingDouble(CardPaymentLog::getAmount)))
                .entrySet().stream()
                .map(entry -> new MerchantSpending(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MerchantSpending::merchant))
                .toList();

        return BankOperationResult.success(
                SpendingsReport.builder()
                        .iban(accountIban)
                        .balance(account.getBalance())
                        .currency(account.getCurrency())
                        .transactions(transactions)
                        .merchants(merchants)
                        .build(
                        )
        );
    }
}
