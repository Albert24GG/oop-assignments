package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.CardPaymentLog;
import org.poo.bank.transaction.impl.GenericLog;
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
            throw new BankOperationException(BankErrorType.CARD_FROZEN);
        }

        BankAccount bankAccount = card.getLinkedAccount();
        double convertedAmount = context.currencyExchangeService()
                .convert(currency, bankAccount.getCurrency(), amount);

        TransactionLog transactionLog;
        if (bankAccount.getBalance() < convertedAmount) {
            transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .description("Insufficient funds")
                    .build();
        } else {
            context.bankAccService().removeFunds(bankAccount, convertedAmount);
            context.cardService().paymentMade(card);

            transactionLog = CardPaymentLog.builder()
                    .timestamp(timestamp)
                    .amount(convertedAmount)
                    .description("Card payment")
                    .merchant(merchant)
                    .build();
        }

        context.transactionService().logTransaction(bankAccount.getIban(), transactionLog);
        return BankOperationResult.success();
    }
}
