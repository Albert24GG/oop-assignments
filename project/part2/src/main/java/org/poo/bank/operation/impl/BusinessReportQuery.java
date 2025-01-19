package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessAccountRole;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.TransferLog;
import org.poo.bank.log.interfaces.TransactionLog;
import org.poo.bank.log.interfaces.UserTransactionLog;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.report.business.BusinessReport;
import org.poo.bank.report.business.BusinessReportType;
import org.poo.bank.report.business.impl.MerchantBusinessReport;
import org.poo.bank.report.business.impl.TransactionBusinessReport;
import org.poo.bank.type.IBAN;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Builder
@RequiredArgsConstructor
public final class BusinessReportQuery extends BankOperation<BusinessReport> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final BusinessReportType type;
    @NonNull
    private final Integer startTimestamp;
    @NonNull
    private final Integer endTimestamp;

    @Override
    protected BankOperationResult<BusinessReport> internalExecute(
            final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() != BankAccountType.BUSINESS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This operation is only available for business accounts");
        }

        switch (type) {
            case TRANSACTION:
                return BankOperationResult.success(
                        getTransactionReport(context, (BusinessAccount) bankAccount));
            case COMMERCIANT:
                return BankOperationResult.success(
                        getMerchantReport(context, (BusinessAccount) bankAccount));
            default:
                throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                        "Invalid report type");
        }
    }

    private MerchantBusinessReport getMerchantReport(final BankOperationContext context,
                                                     final BusinessAccount businessAccount) {
        List<AuditLog> logs =
                context.auditLogService().getLogs(accountIban, startTimestamp, endTimestamp)
                        .stream().filter(log -> log.getLogStatus() == AuditLogStatus.SUCCESS)
                        .toList();

        record UserEntry(UserAccount userAccount, double amount) {
        }

        // Group all user transactions by merchant
        Map<Merchant, List<UserEntry>> merchantTransactions = Stream.concat(
                        logs.stream()
                                .filter(log -> log.getLogType() == AuditLogType.CARD_PAYMENT)
                                .map(log -> (TransactionLog) log),
                        logs.stream()
                                .filter(log -> log.getLogType() == AuditLogType.TRANSFER)
                                .map(log -> (TransferLog) log)
                                .filter(log -> log.getTransferType()
                                        == TransferLog.TransferType.SENT)
                                .filter(log -> log.getRecipientMerchant().isPresent()))
                .filter(log -> businessAccount.getRole(log.getSenderUserAccount().get())
                        .get() != BusinessAccountRole.OWNER)
                .collect(Collectors.groupingBy(
                        log -> log.getRecipientMerchant().get(),
                        Collectors.mapping(
                                log -> new UserEntry(log.getSenderUserAccount().get(),
                                        log.getAmount()),
                                Collectors.toList())));

        // Generate the merchant reports, sorted by the merchant name
        List<MerchantBusinessReport.MerchantReport> merchantReports =
                merchantTransactions.entrySet().stream()
                        .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                        .map(entry -> {
                            Merchant merchant = entry.getKey();
                            Map<UserEntry, Double> userEntries = entry.getValue().stream()
                                    .collect(Collectors.groupingBy(Function.identity(),
                                            Collectors.summingDouble(UserEntry::amount)));
                            return MerchantBusinessReport.MerchantReport.builder()
                                    .merchantName(merchant.getName())
                                    .totalReceived(
                                            userEntries.values().stream()
                                                    .mapToDouble(Double::doubleValue)
                                                    .sum())
                                    .employees(
                                            userEntries.keySet().stream()
                                                    .filter(userEntry ->
                                                            businessAccount.getRole(
                                                                            userEntry.userAccount)
                                                                    .get()
                                                                    == BusinessAccountRole.EMPLOYEE)
                                                    .map(userEntry -> String.format("%s %s",
                                                            userEntry.userAccount().getLastName(),
                                                            userEntry.userAccount().getFirstName()))
                                                    .toList()
                                    )
                                    .managers(
                                            userEntries.keySet().stream()
                                                    .filter(userEntry ->
                                                            businessAccount.getRole(
                                                                            userEntry.userAccount)
                                                                    .get()
                                                                    == BusinessAccountRole.MANAGER)
                                                    .map(userEntry -> String.format("%s %s",
                                                            userEntry.userAccount().getLastName(),
                                                            userEntry.userAccount().getFirstName()))
                                                    .toList()
                                    )
                                    .build();
                        }).toList();
        return MerchantBusinessReport.builder()
                .accountIban(accountIban)
                .balance(businessAccount.getBalance())
                .currency(businessAccount.getCurrency())
                .spendingLimit(businessAccount.getEmployeeSpendingLimit().orElse(Double.MAX_VALUE))
                .depositLimit(businessAccount.getEmployeeDepositLimit().orElse(Double.MAX_VALUE))
                .type(BusinessReportType.COMMERCIANT)
                .merchants(merchantReports)
                .build();
    }

    private TransactionBusinessReport getTransactionReport(final BankOperationContext context,
                                                           final BusinessAccount businessAccount) {
        List<AuditLog> logs =
                context.auditLogService().getLogs(accountIban, startTimestamp, endTimestamp);

        // Group all user transactions by user, filtering based on a predicate, and sum the amounts
        Function<Predicate<AuditLog>, Map<UserAccount, Double>> groupAndSumLogsByUser =
                (logFilter) ->
                        logs.stream()
                                .filter(logFilter)
                                .filter(log -> log.getLogStatus() == AuditLogStatus.SUCCESS)
                                .map(log -> (UserTransactionLog) log)
                                .filter(log ->
                                        businessAccount.getRole(log.getSenderUserAccount().get())
                                                .get()
                                                != BusinessAccountRole.OWNER)
                                .collect(Collectors.groupingBy(
                                        log -> log.getSenderUserAccount().get(),
                                        Collectors.summingDouble(UserTransactionLog::getAmount)));

        // Group all user spending's
        Map<UserAccount, Double> memberSpendings = groupAndSumLogsByUser.apply(
                log -> log.getLogType() == AuditLogType.CARD_PAYMENT
                        || (log.getLogType() == AuditLogType.TRANSFER
                        && ((TransferLog) log).getTransferType() == TransferLog.TransferType.SENT));

        // Group all user deposits
        Map<UserAccount, Double> memberDeposits = groupAndSumLogsByUser.apply(
                log -> log.getLogType() == AuditLogType.DEPOSIT);

        // Set the order of the members based on the order they were added to the account
        List<UserAccount> accountMembers = businessAccount.getAccountMembers();
        Map<UserAccount, Integer> membersOrder = IntStream.range(0, accountMembers.size())
                .boxed()
                .collect(Collectors.toMap(accountMembers::get, Function.identity()));

        // Generate the member reports
        Function<BusinessAccountRole, List<TransactionBusinessReport.MemberReport>>
                generateMemberReports = (role) ->
                businessAccount.getAccountMembers().stream()
                        .filter(member -> businessAccount.getRole(member).get() == role)
                        .sorted(Comparator.comparing(membersOrder::get))
                        .map(member -> TransactionBusinessReport.MemberReport.builder()
                                .name(String.format("%s %s", member.getLastName(),
                                        member.getFirstName()))
                                .spent(memberSpendings.getOrDefault(member, 0.0))
                                .deposited(memberDeposits.getOrDefault(member, 0.0))
                                .build())
                        .toList();

        List<TransactionBusinessReport.MemberReport> employeeReports =
                generateMemberReports.apply(BusinessAccountRole.EMPLOYEE);

        List<TransactionBusinessReport.MemberReport> managerReports =
                generateMemberReports.apply(BusinessAccountRole.MANAGER);

        return TransactionBusinessReport.builder()
                .accountIban(accountIban)
                .balance(businessAccount.getBalance())
                .currency(businessAccount.getCurrency())
                .spendingLimit(businessAccount.getEmployeeSpendingLimit().orElse(Double.MAX_VALUE))
                .depositLimit(businessAccount.getEmployeeDepositLimit().orElse(Double.MAX_VALUE))
                .type(BusinessReportType.TRANSACTION)
                .managers(managerReports)
                .employees(employeeReports)
                .totalSpent(
                        memberSpendings.values().stream().mapToDouble(Double::doubleValue).sum())
                .totalDeposited(
                        memberDeposits.values().stream().mapToDouble(Double::doubleValue).sum())
                .build();

    }
}
