package org.poo.bank.eventSystem.handlers;

import lombok.RequiredArgsConstructor;
import org.poo.bank.eventSystem.BankEventHandler;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.util.BankOperationUtils;

@RequiredArgsConstructor
public final class CashbackEventHandler implements BankEventHandler<TransactionEvent> {
    private final BankOperationContext context;

    @Override
    public void handleEvent(final TransactionEvent event) {
        // If the receiver is not a merchant, skip the cashback
        if (event.getMerchant() == null) {
            return;
        }

        // Calculate the cashback for the transaction
        double cashbackPercentage =
                BankOperationUtils.calculateTransactionCashback(context, event.getMerchant(),
                        event.getSenderBankAccount(), event.getAmount(),
                        event.getCurrency());
        double receivedCashback = event.getAmount() * cashbackPercentage;

        // Convert the cashback to the sender account currency
        double convertedCashback =
                BankOperationUtils.convertCurrency(context, event.getCurrency(),
                        event.getSenderBankAccount().getCurrency(), receivedCashback);

        // Add the cashback to the sender account
        BankOperationUtils.addFunds(context, event.getSenderBankAccount(), convertedCashback);
    }
}
