package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.ValidationUtil;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.GenericLog;
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

        BankAccount sender;
        try {
            sender = ValidationUtil.getBankAccountByIban(context.bankAccService(), senderAccount);
        } catch (IllegalArgumentException e) {
            throw new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND, e.getMessage());
        }
        BankAccount receiver;
        try {
            receiver =
                    ValidationUtil.getBankAccountByAlias(context.bankAccService(), receiverAccount);
        } catch (IllegalArgumentException e) {
            try {
                receiver = ValidationUtil.getBankAccountByIban(context.bankAccService(),
                        IBAN.of(receiverAccount));
            } catch (IllegalArgumentException e2) {
                throw new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND, e2.getMessage());
            }
        }

        double sentAmount = amount;

        TransactionLog sendTransactionLog;
        TransactionLog receiveTransactionLog = null;
        if (sender.getBalance() < sentAmount) {
            sendTransactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .description("Insufficient funds")
                    .build();
        } else {
            double receivedAmount = context.currencyExchangeService()
                    .convert(sender.getCurrency(), receiver.getCurrency(), sentAmount);

            context.bankAccService().removeFunds(sender, sentAmount);
            context.bankAccService().addFunds(receiver, receivedAmount);

            sendTransactionLog = TransferLog.builder()
                    .timestamp(timestamp)
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
        return BankOperationResult.success();
    }
}
