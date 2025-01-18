package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessOperation;
import org.poo.bank.account.UserAccount;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.TransferLog;
import org.poo.bank.type.Email;
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
    @NonNull
    private final Email userEmail;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount sender = BankOperationUtils.getBankAccountByIban(context, senderAccount);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);

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
        // Validate the permissions of the user in case of a business account
        if (sender.getType() == BankAccountType.BUSINESS) {
            BankOperationUtils.validatePermissions(context, (BusinessAccount) sender, userAccount,
                    new BusinessOperation.Transfer(amountWithCommission));
        }

        AuditLog sendAuditLog;
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

            sendAuditLog = TransferLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .logType(AuditLogType.TRANSFER)
                    .description(description)
                    .senderIBAN(sender.getIban())
                    .receiverIBAN(receiverIban)
                    .amount(amount)
                    .currency(sender.getCurrency())
                    .transferType("sent")
                    .build();
            BankOperationUtils.recordLog(context, sender, sendAuditLog);

            // Trigger the transaction event
            context.eventService().post(transactionEvent);

            result = BankOperationResult.success();
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, sender, timestamp,
                    AuditLogType.TRANSFER, e);
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

        AuditLog receiveAuditLog = TransferLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.TRANSFER)
                .description(description)
                .senderIBAN(sender.getIban())
                .receiverIBAN(receiver.getIban())
                .amount(receivedAmount)
                .currency(receiver.getCurrency())
                .transferType("received")
                .build();
        BankOperationUtils.recordLog(context, receiver, receiveAuditLog);
    }
}
