package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessAccountRole;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;

import java.util.Map;

@Builder
@RequiredArgsConstructor
public final class CreateBankAccount extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final Currency currency;
    @NonNull
    private final BankAccountType type;
    private final double interestRate;
    private final int timestamp;

    // Default values for business accounts employee spending and deposit limits (in RON)
    private static final double BUSINESS_EMPLOYEE_DEFAULT_SPENDING_LIMIT = 500.0;
    private static final double BUSINESS_EMPLOYEE_DEFAULT_DEPOSIT_LIMIT = 500.0;
    // Default limits for business account roles
    // The values are in RON
    private static final Map<BusinessAccountRole, Map<String, Double>> BUSINESS_ROLE_LIMITS =
            Map.of(
                    BusinessAccountRole.EMPLOYEE, Map.of(
                            "spendingLimit", BUSINESS_EMPLOYEE_DEFAULT_SPENDING_LIMIT,
                            "depositLimit", BUSINESS_EMPLOYEE_DEFAULT_DEPOSIT_LIMIT
                    )
            );

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount bankAccount = context.bankAccService()
                .createAccount(context.userService().getUser(ownerEmail)
                                .orElseThrow(() -> new BankOperationException(
                                        BankErrorType.USER_NOT_FOUND)), currency, type,
                        interestRate);

        if (type == BankAccountType.BUSINESS) {
            BUSINESS_ROLE_LIMITS.forEach((role, limits) -> {
                double spendingLimit =
                        BankOperationUtils.convertCurrency(context, Currency.of("RON"), currency,
                                limits.get("spendingLimit"));
                double depositLimit =
                        BankOperationUtils.convertCurrency(context, Currency.of("RON"), currency,
                                limits.get("depositLimit"));
                context.bankAccService()
                        .setBusinessAccountSpendingLimit((BusinessAccount) bankAccount, role,
                                spendingLimit);
                context.bankAccService()
                        .setBusinessAccountDepositLimit((BusinessAccount) bankAccount, role,
                                depositLimit);
            });
        }

        AuditLog auditLog = AuditLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.ACCOUNT_CREATION)
                .description("New account created")
                .build();
        BankOperationUtils.recordLog(context, bankAccount, auditLog);

        return BankOperationResult.success();
    }
}
