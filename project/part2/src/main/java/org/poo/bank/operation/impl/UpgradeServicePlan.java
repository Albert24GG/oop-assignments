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
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.FailedOpLog;
import org.poo.bank.transaction.impl.UpgradePlanLog;
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
        BankAccount account = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));
        UserAccount userAccount = account.getOwner();

        double upgradeFee;
        // Perform validations
        try {
            if (newPlan == userAccount.getServicePlanType()) {
                throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                        String.format("The account already has the %s plan",
                                newPlan.toString().toLowerCase()));
            }

            upgradeFee = context.currencyExchangeService().convert(Currency.of("RON"),
                    account.getCurrency(), userAccount.getPlanUpgradeFee(newPlan));
            if (!context.bankAccService().validateFunds(account, upgradeFee)) {
                throw new BankOperationException(BankErrorType.INSUFFICIENT_FUNDS);
            }
        } catch (IllegalArgumentException e) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description(e.getMessage())
                    .build();
            context.transactionLogService().logTransaction(accountIban, transactionLog);

            // Ignore the exception and return a success result
            return BankOperationResult.success();
        }

        context.bankAccService().removeFunds(account, upgradeFee);
        context.userService().upgradePlan(userAccount, newPlan);
        TransactionLog transactionLog = UpgradePlanLog.builder()
                .description("Upgrade plan")
                .timestamp(timestamp)
                .accountIBAN(accountIban)
                .newPlan(newPlan)
                .build();
        context.transactionLogService().logTransaction(accountIban, transactionLog);

        return BankOperationResult.success();
    }
}
