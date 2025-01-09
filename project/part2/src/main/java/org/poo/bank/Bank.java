package org.poo.bank;

import lombok.NonNull;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.UserService;
import org.poo.bank.card.CardService;
import org.poo.bank.currency.CurrencyExchangeService;
import org.poo.bank.eventSystem.BankEventListener;
import org.poo.bank.eventSystem.BankEventService;
import org.poo.bank.eventSystem.events.SplitPaymentEvent;
import org.poo.bank.eventSystem.events.TransactionEvent;
import org.poo.bank.eventSystem.handlers.CashbackEventHandler;
import org.poo.bank.eventSystem.handlers.FreePlanUpgradeHandler;
import org.poo.bank.eventSystem.handlers.SplitPaymentEventHandler;
import org.poo.bank.merchant.CashbackType;
import org.poo.bank.merchant.MerchantService;
import org.poo.bank.merchant.MerchantType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.impl.AddMerchant;
import org.poo.bank.operation.impl.CreateUserAccount;
import org.poo.bank.operation.impl.RegisterExchangeRate;
import org.poo.bank.splitPayment.SplitPaymentService;
import org.poo.bank.log.AuditLogService;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Date;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

public final class Bank {
    private final BankEventService bankEventService = new BankEventService();
    private final CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();
    private final CardService cardService = new CardService();
    private final UserService userService = new UserService();
    private final BankAccService bankAccService = new BankAccService();
    private final AuditLogService auditLogService = new AuditLogService();
    private final MerchantService merchantService = new MerchantService();
    private final SplitPaymentService splitPaymentService =
            new SplitPaymentService(bankEventService);
    private final BankOperationContext bankOperationContext =
            new BankOperationContext(bankAccService,
                    userService, cardService, auditLogService, currencyExchangeService,
                    merchantService, splitPaymentService, bankEventService);

    // Register the event handlers
    {
        bankEventService.subscribe(new BankEventListener<>(SplitPaymentEvent.class,
                new SplitPaymentEventHandler(bankOperationContext)));

        bankEventService.subscribe(new BankEventListener<>(TransactionEvent.class,
                new CashbackEventHandler(bankOperationContext)));

        bankEventService.subscribe(new BankEventListener<>(TransactionEvent.class,
                new FreePlanUpgradeHandler(bankOperationContext)));
    }

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
     * @param firstName  the first name of the user
     * @param lastName   the last name of the user
     * @param email      the email of the user
     * @param birthDate  the birthdate of the user
     * @param occupation the occupation of the user
     * @return the result of the operation
     */
    public BankOperationResult<Void> createUserAccount(@NonNull final String firstName,
                                                       @NonNull final String lastName,
                                                       @NonNull final Email email,
                                                       @NonNull final Date birthDate,
                                                       @NonNull final String occupation) {
        return processOperation(
                new CreateUserAccount(firstName, lastName, email, birthDate, occupation));
    }

    /**
     * Add a new merchant.
     *
     * @param name         the name of the merchant
     * @param id           the id of the merchant
     * @param accountIban  the IBAN of the merchant
     * @param type         the type of the merchant
     * @param cashbackType the cashback type used by the merchant
     * @return the result of the operation
     */
    public BankOperationResult<Void> addMerchant(@NonNull final String name,
                                                 @NonNull final Integer id,
                                                 @NonNull final IBAN accountIban,
                                                 @NonNull final MerchantType type,
                                                 @NonNull final CashbackType cashbackType) {
        return processOperation(new AddMerchant(name, id, accountIban, type, cashbackType));
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
