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
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.CardOpLog;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class CreateCard extends BankOperation<Void> {
    @NonNull
    private final Email userEmail;
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final CardType type;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        // Validate permissions
        if (bankAccount.getType() == BankAccountType.BUSINESS) {
            BankOperationUtils.validatePermissions(context, (BusinessAccount) bankAccount,
                    userAccount,
                    new BusinessOperation.AddCard());
        } else {
            BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);
        }

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
