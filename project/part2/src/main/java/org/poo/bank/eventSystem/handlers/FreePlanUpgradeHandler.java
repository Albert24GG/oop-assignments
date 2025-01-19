package org.poo.bank.eventSystem.handlers;

import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.eventSystem.BankEventHandler;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.impl.UpgradePlanLog;
import org.poo.bank.log.interfaces.TransactionLog;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.servicePlan.ServicePlanType;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.type.Currency;

@RequiredArgsConstructor
public final class FreePlanUpgradeHandler implements BankEventHandler<TransactionEvent> {
    private static final int SILVER_TO_GOLD_TRANSACTION_THRESHOLD = 5;
    /**
     * The minimum amount of a transaction that is eligible for the upgrade (in RON)
     */
    private static final int ELIGIBLE_TRANSACTION_AMOUNT = 300;

    private final BankOperationContext context;

    @Override
    public void handleEvent(final TransactionEvent event) {
        // If the user does not currently have the Silver plan, skip the upgrade
        BankAccount senderBankAccount = event.getSenderBankAccount();
        if (senderBankAccount.getOwner().getServicePlan().getServicePlanType()
                != ServicePlanType.SILVER) {
            return;
        }

        long transactionCount =
                context.auditLogService().getLogs(senderBankAccount.getIban()).stream()
                        .filter(log -> log.getLogStatus() == AuditLogStatus.SUCCESS)
                        .filter(log -> log.getLogType() == AuditLogType.CARD_PAYMENT
                                || log.getLogType() == AuditLogType.TRANSFER)
                        .map(log -> (TransactionLog) log)
                        .filter(log -> log.getRecipientMerchant().isPresent())
                        .filter(log -> BankOperationUtils.convertCurrency(context,
                                senderBankAccount.getCurrency(),
                                Currency.of("RON"), log.getAmount()) >= ELIGIBLE_TRANSACTION_AMOUNT)
                        .count();

        if (transactionCount >= SILVER_TO_GOLD_TRANSACTION_THRESHOLD) {
            context.userService()
                    .upgradePlan(event.getSenderBankAccount().getOwner(), ServicePlanType.GOLD);

            // Register the upgrade in the audit log
            AuditLog log = UpgradePlanLog.builder()
                    .timestamp(event.getTimestamp())
                    .logType(AuditLogType.ACCOUNT_PLAN_UPDATE)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .description("Upgrade plan")
                    .accountIBAN(senderBankAccount.getIban())
                    .newPlanType(ServicePlanType.GOLD)
                    .build();
            BankOperationUtils.recordLog(context, senderBankAccount, log);
        }
    }
}
