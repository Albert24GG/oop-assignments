package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.ValidationUtil;
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
import org.poo.bank.transaction.impl.CardOpLog;
import org.poo.bank.transaction.impl.GenericLog;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class CreateCard extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final CardType type;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        UserAccount userAccount;
        BankAccount bankAccount;
        try {
            userAccount =
                    ValidationUtil.getUserAccount(context.userService(), ownerEmail);
            bankAccount =
                    ValidationUtil.getBankAccountByIban(context.bankAccService(), accountIban);
            ValidationUtil.validateAccountOwnership(bankAccount, userAccount);
        } catch (IllegalArgumentException e) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT, e.getMessage());
        }

        TransactionLog transactionLog;
        // If the user is not the owner of the account, the card creation fails and an error is
        // logged
        if (userAccount != bankAccount.getOwner()) {
            transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .error("Card creation failed")
                    .build();
        } else {
            Card newCard = context.cardService().createCard(bankAccount, type);
            transactionLog = CardOpLog.builder()
                    .timestamp(timestamp)
                    .card(newCard.getNumber())
                    .cardHolder(userAccount.getEmail())
                    .account(bankAccount.getIban())
                    .build();
        }

        context.transactionService().logTransaction(accountIban, transactionLog);
        return BankOperationResult.success();
    }
}
