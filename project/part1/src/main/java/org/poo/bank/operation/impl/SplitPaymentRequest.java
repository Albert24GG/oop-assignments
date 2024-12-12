package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.ValidationUtil;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.impl.SplitPaymentLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Builder
@RequiredArgsConstructor
public final class SplitPaymentRequest extends BankOperation<Void> {
    @NonNull
    private final List<IBAN> involvedAccounts;
    @NonNull
    private final Currency currency;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        if (involvedAccounts.size() < 2) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                    "At least two accounts must be involved in a split payment");
        }

        List<BankAccount> accounts;
        try {
            accounts = involvedAccounts.stream()
                    .map(accountIban -> ValidationUtil.getBankAccountByIban(
                            context.bankAccService(),
                            accountIban))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND, e.getMessage());
        }

        double splitAmount = amount / accounts.size();
        List<Double> amounts = accounts.stream()
                .map(account -> context.currencyExchangeService()
                        .convert(account.getCurrency(), currency, splitAmount))
                .toList();

        // Prepare the transaction log
        SplitPaymentLog transactionLog = SplitPaymentLog.builder()
                .timestamp(timestamp)
                .amount(splitAmount)
                .involvedAccounts(involvedAccounts)
                .currency(currency)
                .build();

        // Check if all accounts have enough funds
        OptionalInt firstAccountWithInsufficientFunds = IntStream.range(0, accounts.size())
                .filter(
                        i -> accounts.get(i).getBalance() < amounts.get(i)
                )
                .findFirst();

        if (firstAccountWithInsufficientFunds.isPresent()) {
            transactionLog = transactionLog.toBuilder()
                    .error("Insufficient funds")
                    .build();
        } else {
            accounts.forEach(account -> context.bankAccService().removeFunds(account, splitAmount));
        }

        SplitPaymentLog finalTransactionLog = transactionLog;
        accounts.forEach(account -> context.transactionService()
                .logTransaction(account.getIban(), finalTransactionLog));
        return BankOperationResult.success();
    }
}
