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
import org.poo.bank.report.MerchantSpending;
import org.poo.bank.report.SpendingsReport;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.CardPaymentLog;
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
    protected BankOperationResult<SpendingsReport> internalExecute(
            final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() == BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This kind of report is not supported for a saving account");
        }

        // Get all card payments
        List<AuditLog>
                transactions =
                context.auditLogService()
                        .getLogs(accountIban, startTimestamp, endTimestamp).stream()
                        .filter(log -> log.getLogType()
                                == AuditLogType.CARD_PAYMENT
                                && log.getLogStatus() == AuditLogStatus.SUCCESS)
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
                        .balance(bankAccount.getBalance())
                        .currency(bankAccount.getCurrency())
                        .transactions(transactions.stream().map(AuditLog::toView).toList())
                        .merchants(merchants)
                        .build()
        );
    }
}
