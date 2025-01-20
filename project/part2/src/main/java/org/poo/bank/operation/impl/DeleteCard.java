package org.poo.bank.operation.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessOperation;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.CardOpLog;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Email;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class DeleteCard extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    @NonNull
    private final Email userEmail;
    /**
     * Whether the card is regenerating due to a one-time use
     */
    private Boolean isRegenerating = false;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);
        BankAccount bankAccount = card.getLinkedAccount();

        // Validate permissions
        if (bankAccount.getType() == BankAccountType.BUSINESS) {
            BusinessOperation op =
                    card.createdBy(userAccount) ? new BusinessOperation.RemoveCardSameOwner()
                            : new BusinessOperation.RemoveCardDifferentOwner();
            try {
                BankOperationUtils.validatePermissions(context, (BusinessAccount) bankAccount,
                        userAccount, op);
            } catch (BankOperationException e) {
                return BankOperationResult.silentError(e.getErrorType());
            }
        } else {
            BankOperationUtils.validateCardOwnership(context, card, userAccount);
        }

        // ??? For some reason, the refs don't want me to actually remove the card from the account,
        // so I will just remove it in case it is regenerating after a one-time use
        if (!isRegenerating) {
            return BankOperationResult.success();
        }

        context.cardService().removeCard(card);
        BankAccount linkedAccount = card.getLinkedAccount();

        AuditLog auditLog = CardOpLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.CARD_DELETION)
                .card(cardNumber)
                .cardHolder(linkedAccount.getOwner().getEmail())
                .account(linkedAccount.getIban())
                .description("The card has been destroyed")
                .build();
        BankOperationUtils.recordLog(context, linkedAccount, auditLog);

        return BankOperationResult.success();
    }
}
