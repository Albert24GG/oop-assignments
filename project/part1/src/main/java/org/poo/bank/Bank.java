package org.poo.bank;

import lombok.NonNull;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.UserService;
import org.poo.bank.card.CardService;
import org.poo.bank.currency.CurrencyExchangeService;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.impl.CreateUserAccount;
import org.poo.bank.operation.impl.RegisterExchangeRate;
import org.poo.bank.transaction.TransactionService;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;


public final class Bank {
    private final CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();
    private final CardService cardService = new CardService();
    private final UserService userService = new UserService();
    private final BankAccService bankAccService = new BankAccService();
    private final TransactionService transactionService = new TransactionService();
    private final BankOperationContext bankOperationContext =
            new BankOperationContext(bankAccService,
                    userService, cardService, transactionService, currencyExchangeService);

    /**
     * Register an exchange rate between two currencies.
     *
     * @param from the currency to convert from
     * @param to   the currency to convert to
     * @param rate the exchange rate
     * @return the result of the operation
     */
    public BankOperationResult<Void> registerExchangeRate(@NonNull final Currency from,
                                                          @NonNull final Currency to,
                                                          final double rate) {
        return processOperation(new RegisterExchangeRate(from, to, rate));
    }

    /**
     * Create a new user.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email of the user
     * @return the result of the operation
     */
    public BankOperationResult<Void> createUserAccount(final String firstName,
                                                       final String lastName,
                                                       @NonNull final Email email) {
        return processOperation(new CreateUserAccount(firstName, lastName, email));
    }


    /**
     * Process a bank operation.
     *
     * @param operation the operation to process
     * @param <T>       the type of the result payload
     * @return the result of the operation
     */
    public <T> BankOperationResult<T> processOperation(@NonNull final BankOperation<T> operation) {
        try {
            return operation.execute(bankOperationContext);
        } catch (BankOperationException e) {
            return BankOperationResult.error(e.getErrorType(), e.getMessage());
        }
    }
}
