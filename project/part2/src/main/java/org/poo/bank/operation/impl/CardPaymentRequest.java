package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.AuditLogType;
import org.poo.bank.transaction.impl.CardPaymentLog;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;

@Builder
@RequiredArgsConstructor
public final class CardPaymentRequest extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final String description;
    @NonNull
    private final Currency currency;
    @NonNull
    private final String merchantName;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        if (amount <= 0) {
            return BankOperationResult.silentError(BankErrorType.INVALID_ARGUMENT,
                    "Amount must be positive");
        }

        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        Merchant merchant = BankOperationUtils.getMerchantByName(context, merchantName);
        BankOperationUtils.validateCardOwnership(context, card, userAccount);

        try {
            BankOperationUtils.validateCardStatus(context, card);
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, card.getLinkedAccount(), timestamp,
                    AuditLogType.CARD_PAYMENT, e);
            return BankOperationResult.silentError(e.getErrorType());
        }

        BankAccount bankAccount = card.getLinkedAccount();
        double convertedAmount =
                BankOperationUtils.convertCurrency(context, currency, bankAccount.getCurrency(),
                        amount);
        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(context, userAccount,
                        convertedAmount, bankAccount.getCurrency());


        try {
            BankOperationUtils.validateFunds(context, bankAccount, amountWithCommission);
            BankOperationUtils.removeFunds(context, bankAccount, amountWithCommission);

            AuditLog auditLog = CardPaymentLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .logType(AuditLogType.CARD_PAYMENT)
                    .description("Card payment")
                    .amount(convertedAmount)
                    .merchant(merchantName)
                    .build();
            BankOperationUtils.recordLog(context, bankAccount, auditLog);

            // Trigger the transaction event
            context.eventService()
                    .post(new TransactionEvent(bankAccount, merchant, amount, currency));

            // If the card is single use, renew it
            if (card.getType() == CardType.SINGLE_USE) {
                new DeleteCard(cardNumber, timestamp).execute(context);
                new CreateCard(ownerEmail, bankAccount.getIban(), card.getType(),
                        timestamp).execute(context);
            }
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, bankAccount, timestamp,
                    AuditLogType.CARD_PAYMENT, e);
            return BankOperationResult.silentError(e.getErrorType());
        }

        return BankOperationResult.success();
    }
}
