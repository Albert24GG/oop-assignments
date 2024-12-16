package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

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

        BankAccount sender = context.bankAccService().getAccountByIban(senderAccount)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));
        BankAccount receiver = context.bankAccService().getAccountByAlias(receiverAccount)
                .orElseGet(() -> context.bankAccService().getAccountByIban(IBAN.of(receiverAccount))
                        .orElseThrow(
                                () -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND)));

        double sentAmount = amount;

        TransactionLog sendTransactionLog;
        Optional<TransactionLog> receiveTransactionLog = Optional.empty();
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
                    .senderIBAN(sender.getIban())
                    .receiverIBAN(receiver.getIban())
                    .amount(sentAmount + " " + sender.getCurrency())
                    .transferType("sent")
                    .build();

            receiveTransactionLog = Optional.of(
                    ((TransferLog) sendTransactionLog).toBuilder()
                            .transferType("received")
                            .amount(receivedAmount + " " + receiver.getCurrency())
                            .build());
        }

        context.transactionLogService().logTransaction(sender.getIban(), sendTransactionLog);
        receiveTransactionLog.ifPresent(
                log -> context.transactionLogService().logTransaction(receiver.getIban(), log));
        return BankOperationResult.success();
    }
}
