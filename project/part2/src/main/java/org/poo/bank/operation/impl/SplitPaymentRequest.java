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
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.splitPayment.SplitPayment;
import org.poo.bank.splitPayment.SplitPaymentType;
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
    @NonNull
    private final SplitPaymentType type;
    @NonNull
    private final List<Double> amountPerAccount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        if (involvedAccounts.size() < 2) {
            throw new BankOperationException(BankErrorType.INVALID_ARGUMENT,
                    "At least two accounts must be involved in a split payment");
        }

        List<BankAccount> bankAccounts = involvedAccounts.stream()
                .map(accountIban -> BankOperationUtils.getBankAccountByIban(context, accountIban))
                .toList();

        // Convert the amount for each account to the currency of the account
        List<Double> convertedAmounts = IntStream.range(0, bankAccounts.size())
                .mapToObj(i -> BankOperationUtils.convertCurrency(context, currency,
                        bankAccounts.get(i).getCurrency(), amountPerAccount.get(i)))
                .toList();

        // Check if all accounts have enough funds
        Optional<BankAccount> firstAccountWithInsufficientFunds =
                IntStream.range(0, bankAccounts.size())
                        .filter(
                                i -> !context.bankAccService()
                                        .validateFunds(bankAccounts.get(i), convertedAmounts.get(i))
                        )
                        .mapToObj(bankAccounts::get)
                        .findFirst();

        // Create the split payment
        SplitPayment splitPayment = SplitPayment.builder()
                .timestamp(timestamp)
                .involvedAccounts(bankAccounts)
                .amountPerAccount(amountPerAccount)
                .currency(currency)
                .type(type)
                .build();

        context.splitPaymentService().registerPayment(splitPayment);

        return BankOperationResult.success();
    }
}
