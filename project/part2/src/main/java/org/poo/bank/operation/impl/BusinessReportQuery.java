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
import org.poo.bank.log.interfaces.UserTransactionLog;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.report.business.BusinessReport;
import org.poo.bank.report.business.BusinessReportType;
import org.poo.bank.report.business.impl.BusinessMemberReport;
import org.poo.bank.report.business.impl.TransactionBusinessReport;
import org.poo.bank.type.IBAN;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
            default:
                throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                        "Invalid report type");
        }
    }


    private TransactionBusinessReport getTransactionReport(final BankOperationContext context,
                                                           final BusinessAccount businessAccount) {
        List<AuditLog> logs =
                context.auditLogService().getLogs(accountIban, startTimestamp, endTimestamp);

        Map<UserAccount, Double> memberSpendings = groupAndSumLogsByUser(logs, businessAccount,
                log -> log.getLogType() == AuditLogType.CARD_PAYMENT
                        || log.getLogType() == AuditLogType.TRANSFER);

        Map<UserAccount, Double> memberDeposits = groupAndSumLogsByUser(logs, businessAccount,
                log -> log.getLogType() == AuditLogType.DEPOSIT);

        List<UserAccount> members = businessAccount.getAccountMembers();

        List<BusinessMemberReport> employeeReports = generateMemberReports(members, businessAccount,
                memberSpendings, memberDeposits, BusinessAccountRole.EMPLOYEE);

        List<BusinessMemberReport> managerReports = generateMemberReports(members, businessAccount,
                memberSpendings, memberDeposits, BusinessAccountRole.MANAGER);

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

    private Map<UserAccount, Double> groupAndSumLogsByUser(final List<AuditLog> logs,
                                                           final BusinessAccount businessAccount,
                                                           final Predicate<AuditLog> logFilter) {
        return logs.stream()
                .filter(logFilter)
                .filter(log -> log.getLogStatus() == AuditLogStatus.SUCCESS)
                .map(log -> (UserTransactionLog) log)
                .filter(log -> businessAccount.getRole(log.getUserAccount()).get()
                        != BusinessAccountRole.OWNER)
                .collect(Collectors.groupingBy(UserTransactionLog::getUserAccount,
                        Collectors.summingDouble(UserTransactionLog::getAmount)));
    }

    private List<BusinessMemberReport> generateMemberReports(
            final List<UserAccount> members,
            final BusinessAccount businessAccount,
            final Map<UserAccount, Double> memberSpendings,
            final Map<UserAccount, Double> memberDeposits,
            final BusinessAccountRole role) {
        return members.stream()
                .filter(member -> businessAccount.getRole(member).get() == role)
                .sorted(Comparator.comparing(UserAccount::getFirstName)
                        .thenComparing(UserAccount::getLastName))
                .map(member -> BusinessMemberReport.builder()
                        .name(member.getLastName() + " " + member.getFirstName())
                        .spent(memberSpendings.getOrDefault(member, 0.0))
                        .deposited(memberDeposits.getOrDefault(member, 0.0))
                        .build())
                .toList();
    }
}
