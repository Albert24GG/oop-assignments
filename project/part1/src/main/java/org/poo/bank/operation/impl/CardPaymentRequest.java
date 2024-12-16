package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardType;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
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
    private final String merchant;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        Card card = context.cardService().getCard(cardNumber)
                .orElseThrow(() -> new BankOperationException(BankErrorType.CARD_NOT_FOUND));
        UserAccount userAccount = context.userService().getUser(ownerEmail)
                .orElseThrow(() -> new BankOperationException(BankErrorType.USER_NOT_FOUND));

        if (!context.cardService().validateCardOwnership(card, userAccount)) {
            throw new BankOperationException(BankErrorType.USER_NOT_CARD_OWNER);
        }

        if (card.getStatus() == Card.Status.FROZEN) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description("The card is frozen")
                    .build();
            context.transactionLogService()
                    .logTransaction(card.getLinkedAccount().getIban(), transactionLog);
            return BankOperationResult.success();
        }

        BankAccount bankAccount = card.getLinkedAccount();
        double convertedAmount = context.currencyExchangeService()
                .convert(currency, bankAccount.getCurrency(), amount);

        if (!context.bankAccService().validateFunds(bankAccount, convertedAmount)) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description("Insufficient funds")
                    .build();
            context.transactionLogService().logTransaction(bankAccount.getIban(), transactionLog);
        } else {
            context.bankAccService().removeFunds(bankAccount, convertedAmount);

            TransactionLog transactionLog = CardPaymentLog.builder()
                    .timestamp(timestamp)
                    .amount(convertedAmount)
                    .description("Card payment")
                    .merchant(merchant)
                    .build();
            context.transactionLogService().logTransaction(bankAccount.getIban(), transactionLog);
            // If the card is single use, renew it
            if (card.getType() == CardType.SINGLE_USE) {
                new DeleteCard(cardNumber, timestamp).execute(context);
                new CreateCard(ownerEmail, bankAccount.getIban(), card.getType(),
                        timestamp).execute(context);
            }
        }

        return BankOperationResult.success();
    }
}
