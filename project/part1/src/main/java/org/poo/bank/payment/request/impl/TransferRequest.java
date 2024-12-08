package org.poo.bank.payment.request.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.account.BankAccount;
import org.poo.bank.payment.PaymentContext;
import org.poo.bank.payment.request.PaymentRequest;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.GenericLog;
import org.poo.bank.transaction.impl.TransferLog;

@SuperBuilder(toBuilder = true)
@Getter
public final class TransferRequest extends PaymentRequest {
    @NonNull
    private final String senderAccount;
    @NonNull
    private final String receiverAccount;
    @NonNull
    private final String description;

    @Override
    protected void internalProcess(PaymentContext context) {
        BankAccount sender = context.bankAccService().getAccount(senderAccount);
        BankAccount receiver = context.bankAccService().getAccount(receiverAccount);

        double sentAmount = getAmount();

        TransactionLog sendTransactionLog;
        TransactionLog receiveTransactionLog = null;
        if (sender.getBalance() < sentAmount) {
            sendTransactionLog = GenericLog.builder()
                    .timestamp(getTimestamp())
                    .description("Insufficient funds")
                    .build();
        } else {
            double receivedAmount = context.currencyExchangeService()
                    .convert(sender.getCurrency(), receiver.getCurrency(), sentAmount);

            context.bankAccService().removeFunds(sender, sentAmount);
            context.bankAccService().addFunds(receiver, receivedAmount);

            sendTransactionLog = TransferLog.builder()
                    .timestamp(getTimestamp())
                    .description(description)
                    .senderIban(sender.getIban())
                    .receiverIban(receiver.getIban())
                    .amount(sentAmount + " " + sender.getCurrency())
                    .transferType("sent")
                    .build();

            receiveTransactionLog = ((TransferLog) sendTransactionLog).toBuilder()
                    .transferType("received")
                    .amount(receivedAmount + " " + receiver.getCurrency())
                    .build();
        }

        context.transactionService().logTransaction(sender.getIban(), sendTransactionLog);
        if (receiveTransactionLog != null) {
            context.transactionService().logTransaction(receiver.getIban(), receiveTransactionLog);
        }
    }
}
