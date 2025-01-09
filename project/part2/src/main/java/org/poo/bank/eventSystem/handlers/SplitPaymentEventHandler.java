package org.poo.bank.eventSystem.handlers;

import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.eventSystem.BankEventHandler;
import org.poo.bank.eventSystem.events.SplitPaymentEvent;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.splitPayment.SplitPayment;
import org.poo.bank.log.impl.SplitPaymentLog;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public final class SplitPaymentEventHandler implements BankEventHandler<SplitPaymentEvent> {
    private final BankOperationContext bankOperationContext;

    @Override
    public void handleEvent(final SplitPaymentEvent event) {
        SplitPaymentLog log = event.getTransactionLog();
        SplitPayment splitPayment = event.getPayment();
        List<BankAccount> involvedAccounts = splitPayment.getAccountsInvolved();
        List<Double> amountPerAccount = splitPayment.getAmountPerAccount();

        // If the split payment was rejected, log the transaction and return
        if (event.getType() == SplitPaymentEvent.Type.REJECTED) {
            involvedAccounts.forEach(account -> {
                BankOperationUtils.recordLog(bankOperationContext, account, log);
            });
            return;
        }

        // Convert the split amount to the currency of each account
        List<Double> convertedAmounts = IntStream.range(0, involvedAccounts.size())
                .mapToObj(i -> BankOperationUtils.convertCurrency(bankOperationContext,
                        splitPayment.getCurrency(),
                        involvedAccounts.get(i).getCurrency(), amountPerAccount.get(i)))
                .toList();

        // Check if all accounts have enough funds
        Optional<BankAccount> firstAccountWithInsufficientFunds =
                IntStream.range(0, involvedAccounts.size())
                        .filter(
                                i -> !bankOperationContext.bankAccService()
                                        .validateFunds(involvedAccounts.get(i),
                                                convertedAmounts.get(i))
                        )
                        .mapToObj(involvedAccounts::get)
                        .findFirst();

        // If any account has insufficient funds, then add an error to the log
        SplitPaymentLog finalLog = firstAccountWithInsufficientFunds
                .<SplitPaymentLog>map(account -> log.toBuilder().error(String.format(
                        "Account %s has insufficient funds for a split payment.",
                        account.getIban())).build())
                .orElseGet(() -> {
                    // If all accounts have enough funds, remove the funds
                    IntStream.range(0, involvedAccounts.size())
                            .forEach(i -> BankOperationUtils.removeFunds(bankOperationContext,
                                    involvedAccounts.get(i),
                                    convertedAmounts.get(i)));
                    return log;
                });

        involvedAccounts.forEach(account -> {
            BankOperationUtils.recordLog(bankOperationContext, account, finalLog);
        });
    }
}
