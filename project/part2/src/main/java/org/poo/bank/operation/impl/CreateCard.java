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
import org.poo.bank.transaction.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.transaction.AuditLog;
import org.poo.bank.transaction.AuditLogType;
import org.poo.bank.transaction.impl.CardOpLog;
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
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);

        Card newCard = context.cardService().createCard(bankAccount, type);
        AuditLog auditLog = CardOpLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.CARD_CREATION)
                .card(newCard.getNumber())
                .cardHolder(userAccount.getEmail())
                .account(bankAccount.getIban())
                .description("New card created")
                .build();
        BankOperationUtils.recordLog(context, bankAccount, auditLog);

        return BankOperationResult.success();
    }
}
