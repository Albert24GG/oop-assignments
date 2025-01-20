package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessOperation;
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
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.CardPaymentLog;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;

@Builder
@RequiredArgsConstructor
public final class CardPaymentRequest extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    @NonNull
    private final Email userEmail;
    @NonNull
    private final String description;
    @NonNull
    private final Currency currency;
    @NonNull
    private final String merchantName;
    @NonNull
    private final Double amount;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        if (amount <= 0) {
            return BankOperationResult.silentError(BankErrorType.INVALID_ARGUMENT,
                    "Amount must be positive");
        }

        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);
        Merchant merchant = BankOperationUtils.getMerchantByName(context, merchantName);
        BankAccount bankAccount = card.getLinkedAccount();

        double convertedAmount =
                BankOperationUtils.convertCurrency(context, currency, bankAccount.getCurrency(),
                        amount);
        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(context,
                        bankAccount.getOwner().getServicePlan(),
                        convertedAmount, bankAccount.getCurrency());

        // Validate permissions
        if (bankAccount.getType() == BankAccountType.BUSINESS) {
            BusinessAccount businessAccount = (BusinessAccount) bankAccount;
            if (!businessAccount.getAccountMembers().contains(userAccount)) {
                //  I don't know why, but this is what the refs say
                return BankOperationResult.error(BankErrorType.CARD_NOT_FOUND);
            }
            try {
                BankOperationUtils.validatePermissions(context, (BusinessAccount) bankAccount,
                        userAccount,
                        new BusinessOperation.CardPayment(amountWithCommission));
            } catch (BankOperationException e) {
                return BankOperationResult.silentError(e.getErrorType());
            }
        } else {
            // For some reason, CARD_NOT_FOUND should be a returned in this case according to the
            // refs
            try {
                BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);
            } catch (BankOperationException e) {
                return BankOperationResult.error(BankErrorType.CARD_NOT_FOUND);
            }
        }

        try {
            BankOperationUtils.validateCardStatus(context, card);
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, card.getLinkedAccount(), timestamp,
                    AuditLogType.CARD_PAYMENT, e);
            return BankOperationResult.silentError(e.getErrorType());
        }

        try {
            BankOperationUtils.validateFunds(context, bankAccount, amountWithCommission);
            BankOperationUtils.removeFunds(context, bankAccount, amountWithCommission);

            AuditLog auditLog = CardPaymentLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .logType(AuditLogType.CARD_PAYMENT)
                    .description("Card payment")
                    .amount(convertedAmount)
                    .merchant(merchant)
                    .userAccount(userAccount)
                    .build();
            BankOperationUtils.recordLog(context, bankAccount, auditLog);

            // Trigger the transaction event
            context.eventService()
                    .post(new TransactionEvent(bankAccount, merchant, amount, currency, timestamp));

            // If the card is single use, renew it
            if (card.getType() == CardType.SINGLE_USE) {
                DeleteCard.builder()
                        .cardNumber(cardNumber)
                        .userEmail(userEmail)
                        .isRegenerating(true)
                        .timestamp(timestamp)
                        .build()
                        .execute(context);
                new CreateCard(userEmail, bankAccount.getIban(), card.getType(),
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
