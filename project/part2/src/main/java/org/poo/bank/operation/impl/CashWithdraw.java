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
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.CashWithdrawLog;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.Location;

@Builder
@RequiredArgsConstructor
public final class CashWithdraw extends BankOperation<Void> {
    @NonNull
    private final CardNumber cardNumber;
    @NonNull
    private final Double amount;
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final Location location;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        Card card = BankOperationUtils.getCardByNumber(context, cardNumber);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);

        // For some reason, CARD_NOT_FOUND should be a returned in this case according to the refs
        try {
            BankOperationUtils.validateCardOwnership(context, card, userAccount);
        } catch (BankOperationException e) {
            return BankOperationResult.error(BankErrorType.CARD_NOT_FOUND);
        }

        BankOperationUtils.validateCardStatus(context, card);

        BankAccount bankAccount = card.getLinkedAccount();
        double convertedAmount = BankOperationUtils.convertCurrency(context, Currency.of("RON"),
                bankAccount.getCurrency(), amount);
        double amountWithCommission =
                BankOperationUtils.calculateAmountWithCommission(context, bankAccount,
                        convertedAmount, bankAccount.getCurrency());

        try {
            BankOperationUtils.validateFunds(context, bankAccount, amountWithCommission);

            BankOperationUtils.removeFunds(context, bankAccount, amountWithCommission);
            AuditLog auditLog = CashWithdrawLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.SUCCESS)
                    .logType(AuditLogType.CASH_WITHDRAWAL)
                    .description("Cash withdrawal of " + amount)
                    .amount(amount)
                    .location(location)
                    .build();
            BankOperationUtils.recordLog(context, bankAccount, auditLog);
        } catch (BankOperationException e) {
            BankOperationUtils.logFailedOperation(context, bankAccount, timestamp,
                    AuditLogType.CASH_WITHDRAWAL, e);
            return BankOperationResult.silentError(e.getErrorType());
        }

        return BankOperationResult.success();
    }
}
