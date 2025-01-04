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
import org.poo.bank.transaction.impl.CashWithdrawLog;
import org.poo.bank.transaction.impl.FailedOpLog;
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
                .convert(Currency.of("RON"), bankAccount.getCurrency(), amount);
        double amountWithCommission = convertedAmount
                * (1 + userAccount.getServicePlan().getTransactionCommission(convertedAmount));

        if (!context.bankAccService().validateFunds(bankAccount, amountWithCommission)) {
            TransactionLog transactionLog = FailedOpLog.builder()
                    .timestamp(timestamp)
                    .description("Insufficient funds")
                    .build();
            context.transactionLogService().logTransaction(bankAccount.getIban(), transactionLog);
            return BankOperationResult.success();
        }

        context.bankAccService().removeFunds(bankAccount, amountWithCommission);
        TransactionLog transactionLog = CashWithdrawLog.builder()
                .description("Cash withdrawal of " + amount)
                .amount(amount)
                .location(location)
                .timestamp(timestamp)
                .build();
        context.transactionLogService().logTransaction(bankAccount.getIban(), transactionLog);
        return BankOperationResult.success();
    }
}
