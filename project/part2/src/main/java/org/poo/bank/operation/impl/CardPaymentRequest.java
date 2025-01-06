package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.CardPaymentLog;
import org.poo.bank.transaction.impl.FailedOpLog;
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
        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        BankOperationUtils.validateCardOwnership(context, card, userAccount);

        try {
            BankOperationUtils.validateCardStatus(context, card);
        } catch (BankOperationException e) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description(e.getMessage())
                    .build();
            context.transactionLogService()
                    .logTransaction(card.getLinkedAccount().getIban(), transactionLog);
            return BankOperationResult.success();
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

            TransactionLog transactionLog = CardPaymentLog.builder()
                    .timestamp(timestamp)
                    .amount(convertedAmount)
                    .description("Card payment")
                    .merchant(merchantName)
                    .build();
            BankOperationUtils.logTransaction(context, bankAccount, transactionLog);
            // If the card is single use, renew it
            if (card.getType() == CardType.SINGLE_USE) {
                new DeleteCard(cardNumber, timestamp).execute(context);
                new CreateCard(ownerEmail, bankAccount.getIban(), card.getType(),
                        timestamp).execute(context);
            }
        } catch (BankOperationException e) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description(e.getMessage())
                    .build();
            BankOperationUtils.logTransaction(context, bankAccount, transactionLog);
        }

        return BankOperationResult.success();
    }
}
