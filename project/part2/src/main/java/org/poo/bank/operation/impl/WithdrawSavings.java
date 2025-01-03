package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.impl.FailedOpLog;
import org.poo.bank.transaction.impl.SavingsWithdrawLog;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.time.LocalDate;
import java.util.List;

@Builder
@RequiredArgsConstructor
public final class WithdrawSavings extends BankOperation<Void> {
    private static final int MIN_AGE_TO_WITHDRAW = 21;

    @NonNull
    private final IBAN accountIban;
    private final double amount;
    @NonNull
    private final Currency currency;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount savingsAccount = context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));
        UserAccount userAccount = savingsAccount.getOwner();

        List<BankAccount> classicAccounts;
        double amountToWithdraw;

        // Perform validations
        try {

            int age = userAccount.getBirthDate().until(LocalDate.now()).getYears();
            if (age < MIN_AGE_TO_WITHDRAW) {
                throw new BankOperationException(BankErrorType.AGE_RESTRICTION);
            }


            classicAccounts = userAccount.getAccounts().stream()
                    .filter(account -> account.getType() == BankAccountType.CLASSIC).toList();

            // Other verifications
            if (classicAccounts.isEmpty()) {
                throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                        "You do not have a classic account.");
            }

            if (savingsAccount.getType() != BankAccountType.SAVINGS) {
                throw new BankOperationException(BankErrorType.INVALID_ACCOUNT_TYPE,
                        "Account is not of type savings.");
            }


            // Valid operation, proceed with the withdrawal
            amountToWithdraw = context.currencyExchangeService()
                    .convert(currency, savingsAccount.getCurrency(), amount);

            if (!context.bankAccService().validateFunds(savingsAccount, amountToWithdraw)) {
                throw new BankOperationException(BankErrorType.INSUFFICIENT_FUNDS);
            }
        } catch (BankOperationException e) {
            TransactionLog transactionLog =
                    FailedOpLog.builder().description(e.getMessage()).timestamp(timestamp).build();
            context.transactionLogService()
                    .logTransaction(savingsAccount.getIban(), transactionLog);
            // Ignore the error and return success
            return BankOperationResult.success();
        }

        BankAccount destinationAccount =
                classicAccounts.stream().filter(account -> account.getCurrency() == currency)
                        .findFirst().orElseThrow(
                                () -> new BankOperationException(BankErrorType.INVALID_OPERATION));
        context.bankAccService().addFunds(destinationAccount, amount);
        context.bankAccService().removeFunds(savingsAccount, amountToWithdraw);

        TransactionLog transactionLog =
                SavingsWithdrawLog.builder().amount(amount).timestamp(timestamp)
                        .classicAccountIBAN(destinationAccount.getIban())
                        .savingsAccountIBAN(savingsAccount.getIban()).build();
        context.transactionLogService().logTransaction(savingsAccount.getIban(), transactionLog);

        return BankOperationResult.success();
    }
}
