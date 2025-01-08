package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.FailedOpLog;
import org.poo.bank.transaction.impl.TransferLog;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class TransferRequest extends BankOperation<Void> {
    @NonNull
    private final IBAN senderAccount;
    /**
     * The IBAN or account alias of the receiver
     */
    @NonNull
    private final String receiverAccount;
    @NonNull
    private final String description;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount sender = BankOperationUtils.getBankAccountByIban(context, senderAccount);


        Merchant merchant = null;
        BankAccount receiver = null;

        // Check if the receiver is a merchant or a user
        try {
            merchant =
                    BankOperationUtils.getMerchantByIban(context, IBAN.of(receiverAccount));
        } catch (BankOperationException e) {
            receiver =
                    BankOperationUtils.getBankAccountByAliasOrIban(context, receiverAccount);
        }

        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(context, sender, amount,
                        sender.getCurrency());

        TransactionLog sendTransactionLog;
        BankOperationResult<Void> result;

        try {
            BankOperationUtils.validateFunds(context, sender, amountWithCommission);
            BankOperationUtils.removeFunds(context, sender, amountWithCommission);

            IBAN receiverIban;
            // Handle the transfer depending on the receiver type

            TransactionEvent transactionEvent;
            if (receiver != null) {
                transferToUser(context, sender, receiver);
                receiverIban = receiver.getIban();
                transactionEvent =
                        new TransactionEvent(sender, receiver, amount, sender.getCurrency());
            } else {
                receiverIban = merchant.getAccountIban();
                transactionEvent =
                        new TransactionEvent(sender, merchant, amount, sender.getCurrency());
            }

            sendTransactionLog = TransferLog.builder()
                    .timestamp(timestamp)
                    .description(description)
                    .senderIBAN(sender.getIban())
                    .receiverIBAN(receiverIban)
                    .amount(amount)
                    .currency(sender.getCurrency())
                    .transferType("sent")
                    .build();
            BankOperationUtils.logTransaction(context, sender, sendTransactionLog);

            // Trigger the transaction event
            context.eventService().post(transactionEvent);

            result = BankOperationResult.success();
        } catch (BankOperationException e) {
            sendTransactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description(e.getMessage())
                    .build();
            BankOperationUtils.logTransaction(context, sender, sendTransactionLog);
            result = BankOperationResult.silentError(e.getErrorType());
        }

        return result;
    }

    private void transferToUser(final BankOperationContext context,
                                final BankAccount sender,
                                final BankAccount receiver) {
        double receivedAmount = context.currencyExchangeService()
                .convert(sender.getCurrency(), receiver.getCurrency(), amount);
        BankOperationUtils.addFunds(context, receiver, receivedAmount);

        TransactionLog receiveTransactionLog = TransferLog.builder()
                .timestamp(timestamp)
                .description(description)
                .senderIBAN(sender.getIban())
                .receiverIBAN(receiver.getIban())
                .amount(receivedAmount)
                .currency(receiver.getCurrency())
                .transferType("received")
                .build();
        BankOperationUtils.logTransaction(context, receiver, receiveTransactionLog);
    }
}
