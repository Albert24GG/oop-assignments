package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.card.Card;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.CardOpLog;
import org.poo.bank.type.CardNumber;

@Builder
@RequiredArgsConstructor
public final class DeleteCard extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        Card card = context.cardService().getCard(cardNumber)
                .orElseThrow(() -> new BankOperationException(BankErrorType.CARD_NOT_FOUND));

        context.cardService().removeCard(card);
        BankAccount linkedAccount = card.getLinkedAccount();

        TransactionLog transactionLog = CardOpLog.builder()
                .timestamp(timestamp)
                .card(cardNumber)
                .cardHolder(linkedAccount.getOwner().getEmail())
                .account(linkedAccount.getIban())
                .description("The card has been destroyed")
                .build();
        context.transactionLogService().logTransaction(linkedAccount.getIban(), transactionLog);

        return BankOperationResult.success();
    }
}
