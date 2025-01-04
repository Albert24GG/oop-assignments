package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
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
        BankAccount receiver =
                BankOperationUtils.getBankAccountByAliasOrIban(context, receiverAccount);

        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(sender, amount);

        TransactionLog sendTransactionLog;

        try {
            BankOperationUtils.validateFunds(context, sender, amountWithCommission);

            double receivedAmount = context.currencyExchangeService()
                    .convert(sender.getCurrency(), receiver.getCurrency(), amount);

            BankOperationUtils.removeFunds(context, sender, amountWithCommission);
            BankOperationUtils.addFunds(context, receiver, receivedAmount);

            sendTransactionLog = TransferLog.builder()
                    .timestamp(timestamp)
                    .description(description)
                    .senderIBAN(sender.getIban())
                    .receiverIBAN(receiver.getIban())
                    .amount(amount + " " + sender.getCurrency())
                    .transferType("sent")
                    .build();

            TransactionLog receiveTransactionLog = ((TransferLog) sendTransactionLog).toBuilder()
                    .transferType("received")
                    .amount(receivedAmount + " " + receiver.getCurrency())
                    .build();
            BankOperationUtils.logTransaction(context, receiver, receiveTransactionLog);
        } catch (BankOperationException e) {
            sendTransactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description(e.getMessage())
                    .build();
        }

        BankOperationUtils.logTransaction(context, sender, sendTransactionLog);
        return BankOperationResult.success();
    }
}
