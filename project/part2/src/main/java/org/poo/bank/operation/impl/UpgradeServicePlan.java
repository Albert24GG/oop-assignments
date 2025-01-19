package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.UpgradePlanLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;
import org.poo.bank.servicePlan.ServicePlanType;

@Builder
@RequiredArgsConstructor
public final class UpgradeServicePlan extends BankOperation<Void> {
    @NonNull
    private final ServicePlanType newPlan;
    @NonNull
    private final IBAN accountIban;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);
        UserAccount userAccount = bankAccount.getOwner();

        ServicePlan servicePlan = userAccount.getServicePlan();
        double upgradeFee;
        // Perform validations
        try {
            if (newPlan == servicePlan.getServicePlanType()) {
                throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                        String.format("The user already has the %s plan.",
                                newPlan.toString().toLowerCase()));
            }
            upgradeFee = servicePlan.getUpgradeFee(newPlan).orElseThrow(
                    () -> new BankOperationException(BankErrorType.INVALID_OPERATION,
                            String.format("The %s plan cannot be upgraded to %s",
                                    servicePlan.getServicePlanType().toString().toLowerCase(),
                                    newPlan.toString().toLowerCase())));

            upgradeFee = BankOperationUtils.convertCurrency(context, Currency.of("RON"),
                    bankAccount.getCurrency(), upgradeFee);

            BankOperationUtils.validateFunds(context, bankAccount, upgradeFee);
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, bankAccount, timestamp,
                    AuditLogType.ACCOUNT_PLAN_UPDATE, e);
            return BankOperationResult.silentError(e.getErrorType());
        }

        BankOperationUtils.removeFunds(context, bankAccount, upgradeFee);
        context.userService().upgradePlan(userAccount, newPlan);
        AuditLog auditLog = UpgradePlanLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.ACCOUNT_PLAN_UPDATE)
                .description("Upgrade plan")
                .accountIBAN(accountIban)
                .newPlanType(newPlan)
                .build();
        BankOperationUtils.recordLog(context, bankAccount, auditLog);

        return BankOperationResult.success();
    }
}
