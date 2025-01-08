package org.poo.bank.eventSystem.handlers;

import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.eventSystem.BankEventHandler;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.servicePlan.ServicePlanType;
import org.poo.bank.transaction.TransactionLogType;

@RequiredArgsConstructor
public class FreePlanUpgradeHandler implements BankEventHandler<TransactionEvent> {
    private static final int SILVER_TO_GOLD_TRANSACTION_THRESHOLD = 5;

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
                context.transactionLogService().getLogs(senderBankAccount.getIban())
                        .stream().filter(log -> log.getType() == TransactionLogType.CARD_PAYMENT ||
                                log.getType() == TransactionLogType.TRANSFER)
                        .count();

        if (transactionCount >= SILVER_TO_GOLD_TRANSACTION_THRESHOLD) {
            context.userService()
                    .upgradePlan(event.getSenderBankAccount().getOwner(), ServicePlanType.GOLD);
        }
    }
}
