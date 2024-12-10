package org.poo.bank.payment.request.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.ValidationUtil;
import org.poo.bank.account.BankAccount;
import org.poo.bank.payment.PaymentContext;
import org.poo.bank.payment.request.PaymentRequest;
import org.poo.bank.transaction.impl.SplitPaymentLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@SuperBuilder(toBuilder = true)
@Getter
public final class SplitPaymentRequest extends PaymentRequest {
    @NonNull
    private final List<IBAN> involvedAccounts;
    @NonNull
    private final Currency currency;

    @Override
    protected void internalProcess(@NonNull final PaymentContext context) {
        if (involvedAccounts.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two accounts must be involved in a split payment");
        }

        List<BankAccount> accounts = involvedAccounts.stream()
                .map(accountIban -> ValidationUtil.getBankAccountByIban(context.bankAccService(),
                        accountIban))
                .toList();

        double splitAmount = getAmount() / accounts.size();
        List<Double> amounts = accounts.stream()
                .map(account -> context.currencyExchangeService()
                        .convert(account.getCurrency(), currency, splitAmount))
                .toList();

        // Prepare the transaction log
        SplitPaymentLog transactionLog = SplitPaymentLog.builder()
                .timestamp(getTimestamp())
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
    }
}
