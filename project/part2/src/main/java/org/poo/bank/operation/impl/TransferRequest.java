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
    /**
     * The IBAN or account alias of the sender
     */
    @NonNull
    private final String senderIdentifier;
    /**
     * The IBAN or account alias of the receiver
     */
    @NonNull
    private final String receiverIdentifier;
    @NonNull
    private final String description;
    @NonNull
    private final Email userEmail;
    @NonNull
    private final Double amount;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount senderAccount =
                BankOperationUtils.getBankAccountByAliasOrIban(context, senderIdentifier);
        UserAccount senderUserAccount = BankOperationUtils.getUserByEmail(context, userEmail);

        Merchant merchant = null;
        BankAccount receiverAccount = null;

        // Check if the receiver is a merchant or a user
        try {
            merchant =
                    BankOperationUtils.getMerchantByIban(context, IBAN.of(receiverIdentifier));
        } catch (BankOperationException e) {
            receiverAccount =
                    BankOperationUtils.getBankAccountByAliasOrIban(context, receiverIdentifier);
        }

        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(context, senderAccount, amount,
                        senderAccount.getCurrency());
        // Validate the permissions of the user in case of a business account
        if (senderAccount.getType() == BankAccountType.BUSINESS) {
            try {
                BankOperationUtils.validatePermissions(context, (BusinessAccount) senderAccount,
                        senderUserAccount,
                        new BusinessOperation.Transfer(amountWithCommission));
            } catch (BankOperationException e) {
                return BankOperationResult.silentError(e.getErrorType());
            }
        }

        AuditLog sendAuditLog;
        BankOperationResult<Void> result;

        try {
            BankOperationUtils.validateFunds(context, senderAccount, amountWithCommission);
            BankOperationUtils.removeFunds(context, senderAccount, amountWithCommission);

            IBAN receiverIban;
            // Handle the transfer depending on the receiverAccount type

            TransactionEvent transactionEvent;
            if (receiverAccount != null) {
                transferToUser(context, senderAccount, senderUserAccount, receiverAccount);
                receiverIban = receiverAccount.getIban();
                transactionEvent =
                        new TransactionEvent(senderAccount, receiverAccount, amount,
                                senderAccount.getCurrency(), timestamp);
            } else {
                receiverIban = merchant.getAccountIban();
                transactionEvent =
                        new TransactionEvent(senderAccount, merchant, amount,
                                senderAccount.getCurrency(), timestamp);
            }

            sendAuditLog = TransferLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .initiatingUser(senderUserAccount)
                    .merchant(merchant)
                    .logType(AuditLogType.TRANSFER)
                    .description(description)
                    .senderIBAN(senderAccount.getIban())
                    .receiverIBAN(receiverIban)
                    .amount(amount)
                    .currency(senderAccount.getCurrency())
                    .transferType(TransferLog.TransferType.SENT)
                    .build();
            BankOperationUtils.recordLog(context, senderAccount, sendAuditLog);

            // Trigger the transaction event
            context.eventService().post(transactionEvent);

            result = BankOperationResult.success();
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, senderAccount, timestamp,
                    AuditLogType.TRANSFER, e);
            result = BankOperationResult.silentError(e.getErrorType());
        }

        return result;
    }

    private void transferToUser(final BankOperationContext context,
                                final BankAccount senderAccount,
                                final UserAccount senderUserAccount,
                                final BankAccount receiverAccount) {
        double receivedAmount = context.currencyExchangeService()
                .convert(senderAccount.getCurrency(), receiverAccount.getCurrency(), amount);
        BankOperationUtils.addFunds(context, receiverAccount, receivedAmount);

        AuditLog receiveAuditLog = TransferLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .initiatingUser(senderUserAccount)
                .logType(AuditLogType.TRANSFER)
                .description(description)
                .senderIBAN(senderAccount.getIban())
                .receiverIBAN(receiverAccount.getIban())
                .amount(receivedAmount)
                .currency(receiverAccount.getCurrency())
                .transferType(TransferLog.TransferType.RECEIVED)
                .build();
        BankOperationUtils.recordLog(context, receiverAccount, receiveAuditLog);
    }
}
