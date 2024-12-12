package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.SplitPaymentLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;
import java.util.Optional;
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

        List<BankAccount> accounts = involvedAccounts.stream()
                .map(accountIban -> context.bankAccService().getAccountByIban(accountIban)
                        .orElseThrow(
                                () -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND)))
                .toList();

        double splitAmount = amount / accounts.size();
        List<Double> amounts = accounts.stream()
                .map(account -> context.currencyExchangeService()
                        .convert(currency, account.getCurrency(), splitAmount))
                .toList();

        // Check if all accounts have enough funds
        Optional<BankAccount> lastAccountWithInsufficientFunds = IntStream.range(0, accounts.size())
                .filter(
                        i -> accounts.get(i).getBalance() < amounts.get(i)
                )
                .reduce((a, b) -> b).stream()
                .mapToObj(accounts::get)
                .findFirst();

        // Prepare the transaction log
        var logBuilder = SplitPaymentLog.builder()
                .timestamp(timestamp)
                .amount(splitAmount)
                .involvedAccounts(involvedAccounts)
                .currency(currency);


        TransactionLog transactionLog = lastAccountWithInsufficientFunds.<TransactionLog>map(
                account -> logBuilder
                        .error(String.format(
                                "Account %s has insufficient funds for a split payment",
                                account.getIban()))
                        .build()
        ).orElseGet(
                () -> {
                    IntStream.range(0, accounts.size())
                            .forEach(i -> context.bankAccService()
                                    .removeFunds(accounts.get(i), amounts.get(i)));
                    return logBuilder
                            .description(
                                    String.format("Split payment of %.2f %s", amount, currency))
                            .build();
                }
        );
        accounts.forEach(account -> context.transactionService()
                .logTransaction(account.getIban(), transactionLog));
        return BankOperationResult.success();
    }
}
