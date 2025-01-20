package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.card.Card;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.type.CardNumber;

@Builder
@RequiredArgsConstructor
public final class CheckCardStatus extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);

        if (context.cardService().updateCardStatus(card) == Card.Status.TO_BE_FROZEN) {
            AuditLog log = AuditLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.FAILURE)
                    .logType(AuditLogType.CARD_STATUS_CHECK)
                    .description(
                            "You have reached the minimum amount of funds, the card will be frozen")
                    .build();
            BankOperationUtils.recordLog(context, card, log);
        }
        return BankOperationResult.success();
    }
}
