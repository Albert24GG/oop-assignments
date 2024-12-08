package org.poo.bank.payment.request.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.ValidationUtil;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.payment.PaymentContext;
import org.poo.bank.payment.request.PaymentRequest;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.CardPaymentLog;
import org.poo.bank.transaction.impl.GenericLog;

@SuperBuilder(toBuilder = true)
@Getter
public final class CardPaymentRequest extends PaymentRequest {
    @NonNull
    private final String cardNumber;
    @NonNull
    private final String ownerEmail;
    @NonNull
    private final String description;
    @NonNull
    private final String currency;
    @NonNull
    private final String merchant;


    @Override
    protected void internalProcess(PaymentContext context) {
        Card card = ValidationUtil.getCard(context.cardService(), cardNumber);
        UserAccount userAccount = ValidationUtil.getUserAccount(context.userService(), ownerEmail);
        ValidationUtil.validateCardOwnership(card, userAccount);
        ValidationUtil.validateCardNotFrozen(card);

        BankAccount bankAccount = card.getLinkedAccount();
        double convertedAmount = context.currencyExchangeService()
                .convert(currency, bankAccount.getCurrency(), getAmount());

        TransactionLog transactionLog;
        if (bankAccount.getBalance() < convertedAmount) {
            transactionLog = GenericLog.builder()
                    .timestamp(getTimestamp())
                    .description("Insufficient funds")
                    .build();
        } else {
            context.bankAccService().removeFunds(bankAccount, convertedAmount);
            context.cardService().paymentMade(card);

            transactionLog = CardPaymentLog.builder()
                    .timestamp(getTimestamp())
                    .amount(convertedAmount)
                    .description(description)
                    .merchant(merchant)
                    .build();
        }

        context.transactionService().logTransaction(bankAccount.getIban(), transactionLog);
    }
}
