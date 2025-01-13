package org.poo.bank.operation.util;

import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessOperation;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.Card;
import org.poo.bank.merchant.Discount;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

public final class BankOperationUtils {
    private BankOperationUtils() {
    }

    /**
     * Get the card associated with the card number
     *
     * @param context    The bank operation context
     * @param cardNumber The card number
     * @return The card
     * @throws BankOperationException If the card is not found
     */
    public static Card getCardByNumber(final BankOperationContext context,
                                       final CardNumber cardNumber) throws BankOperationException {
        return context.cardService().getCard(cardNumber)
                .orElseThrow(() -> new BankOperationException(BankErrorType.CARD_NOT_FOUND));
    }

    /**
     * Get the user associated with the email
     *
     * @param context    The bank operation context
     * @param ownerEmail The email of the user
     * @return The user
     * @throws BankOperationException If the user is not found
     */
    public static UserAccount getUserByEmail(final BankOperationContext context,
                                             final Email ownerEmail) throws BankOperationException {
        return context.userService().getUser(ownerEmail)
                .orElseThrow(() -> new BankOperationException(BankErrorType.USER_NOT_FOUND));
    }

    /**
     * Get the bank account associated with the IBAN
     *
     * @param context     The bank operation context
     * @param accountIban The IBAN of the account
     * @return The bank account
     * @throws BankOperationException If the bank account is not found
     */
    public static BankAccount getBankAccountByIban(final BankOperationContext context,
                                                   final IBAN accountIban)
            throws BankOperationException {
        return context.bankAccService().getAccountByIban(accountIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.ACCOUNT_NOT_FOUND));
    }

    /**
     * Get the bank account associated with the alias or IBAN
     * It first tries to get the account by alias and then by IBAN
     *
     * @param context            The bank operation context
     * @param accountAliasOrIban The alias or IBAN of the account
     * @return The bank account
     * @throws BankOperationException If the bank account is not found
     */
    public static BankAccount getBankAccountByAliasOrIban(final BankOperationContext context,
                                                          final String accountAliasOrIban)
            throws BankOperationException {
        return context.bankAccService().getAccountByAlias(accountAliasOrIban)
                .orElseGet(
                        () -> context.bankAccService().getAccountByIban(IBAN.of(accountAliasOrIban))
                                .orElseThrow(
                                        () -> new BankOperationException(
                                                BankErrorType.USER_NOT_FOUND)));
    }

    /**
     * Get the merchant associated with the name
     *
     * @param context      The bank operation context
     * @param merchantName The name of the merchant
     * @return The merchant
     * @throws BankOperationException If the merchant is not found
     */
    public static Merchant getMerchantByName(final BankOperationContext context,
                                             final String merchantName)
            throws BankOperationException {
        return context.merchantService().getMerchant(merchantName)
                .orElseThrow(() -> new BankOperationException(BankErrorType.MERCHANT_NOT_FOUND));
    }

    /**
     * Get the merchant associated with the IBAN
     *
     * @param context      The bank operation context
     * @param merchantIban The IBAN of the merchant
     * @return The merchant
     * @throws BankOperationException If the merchant is not found
     */
    public static Merchant getMerchantByIban(final BankOperationContext context,
                                             final IBAN merchantIban)
            throws BankOperationException {
        return context.merchantService().getMerchant(merchantIban)
                .orElseThrow(() -> new BankOperationException(BankErrorType.MERCHANT_NOT_FOUND));
    }

    /**
     * Get the card associated with the card number and validate that the card is owned by the user
     * with the given email
     *
     * @param context     The bank operation context
     * @param card        The card
     * @param userAccount The card number
     * @throws BankOperationException If the card is not owned by the user
     */
    public static void validateCardOwnership(final BankOperationContext context,
                                             final Card card, final
                                             UserAccount userAccount)
            throws BankOperationException {
        if (!context.cardService().validateCardOwnership(card, userAccount)) {
            throw new BankOperationException(BankErrorType.USER_NOT_CARD_OWNER);
        }
    }

    /**
     * Validate that the user is the owner of the bank account
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param userAccount The user account
     * @throws BankOperationException If the user is not the owner of the bank account
     */
    public static void validateAccountOwnership(final BankOperationContext context,
                                                final BankAccount bankAccount, final
                                                UserAccount userAccount)
            throws BankOperationException {
        if (!context.bankAccService().validateAccountOwnership(bankAccount, userAccount)) {
            throw new BankOperationException(BankErrorType.USER_NOT_ACCOUNT_OWNER);
        }
    }

    /**
     * Validate the status of the card
     *
     * @param context The bank operation context
     * @param card    The card
     * @throws BankOperationException If the card is frozen
     */
    public static void validateCardStatus(final BankOperationContext context,
                                          final Card card) throws BankOperationException {
        if (card.getStatus() == Card.Status.FROZEN) {
            throw new BankOperationException(BankErrorType.CARD_FROZEN);
        }
    }

    /**
     * Validate that the bank account has sufficient funds
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param amount      The amount to be withdrawn
     * @throws BankOperationException If the bank account has insufficient funds
     */
    public static void validateFunds(final BankOperationContext context,
                                     final BankAccount bankAccount, final double amount)
            throws BankOperationException {
        if (!context.bankAccService().validateFunds(bankAccount, amount)) {
            throw new BankOperationException(BankErrorType.INSUFFICIENT_FUNDS);
        }
    }

    /**
     * Record a log
     *
     * @param context     The bank operation context
     * @param accountIban The IBAN of the account
     * @param auditLog    The log to be recorded
     */
    public static void recordLog(final BankOperationContext context,
                                 final IBAN accountIban,
                                 final AuditLog auditLog) {
        context.auditLogService().recordLog(accountIban, auditLog);
    }

    /**
     * Record a log
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param auditLog    The log to be recorded
     */
    public static void recordLog(final BankOperationContext context,
                                 final BankAccount bankAccount,
                                 final AuditLog auditLog) {
        recordLog(context, bankAccount.getIban(), auditLog);
    }

    /**
     * Log a failed operation
     *
     * @param context     The bank operation context
     * @param accountIban The IBAN of the account
     * @param timestamp   The timestamp of the operation
     * @param logType     The type of the log
     * @param e           The exception that caused the failure
     */
    public static void logFailedOperation(final BankOperationContext context,
                                          final IBAN accountIban, final int timestamp, final
                                          AuditLogType logType, final BankOperationException e) {
        AuditLog log = AuditLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.FAILURE)
                .logType(logType)
                .description(e.getMessage())
                .build();
        recordLog(context, accountIban, log);
    }

    /**
     * Log a failed operation
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param timestamp   The timestamp of the operation
     * @param logType     The type of the log
     * @param e           The exception that caused the failure
     */
    public static void logFailedOperation(final BankOperationContext context,
                                          final BankAccount bankAccount, final int timestamp, final
                                          AuditLogType logType, final BankOperationException e) {
        logFailedOperation(context, bankAccount.getIban(), timestamp, logType, e);
    }

    /**
     * Log a transaction
     *
     * @param context  The bank operation context
     * @param card     The card
     * @param auditLog The transaction log
     */
    public static void recordLog(final BankOperationContext context,
                                 final Card card,
                                 final AuditLog auditLog) {
        recordLog(context, card.getLinkedAccount().getIban(), auditLog);
    }

    /**
     * Calculate the amount to be withdrawn from the bank account for any transaction (including
     * commission)
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param amount      The amount to be withdrawn
     * @param currency    The currency of the amount
     * @return The amount to be withdrawn (including commission)
     */
    public static double calculateAmountWithCommission(final BankOperationContext context,
                                                       final BankAccount bankAccount,
                                                       final double amount,
                                                       final Currency currency) {
        return calculateAmountWithCommission(context, bankAccount.getOwner(), amount, currency);
    }

    /**
     * Calculate the amount to be withdrawn from the bank account for any transaction (including
     * commission)
     *
     * @param context     The bank operation context
     * @param userAccount The user account
     * @param amount      The amount to be withdrawn
     * @param currency    The currency of the amount
     * @return The amount to be withdrawn (including commission)
     */
    public static double calculateAmountWithCommission(final BankOperationContext context,
                                                       final UserAccount userAccount,
                                                       final double amount,
                                                       final Currency currency) {
        // Convert the amount to RON
        double convertedAmount = convertCurrency(context, currency,
                Currency.of("RON"), amount);
        return amount
                * (1 + userAccount.getServicePlan().getTransactionCommission(convertedAmount));
    }

    /**
     * Convert an amount from one currency to another
     *
     * @param context      The bank operation context
     * @param currencyFrom The currency to convert from
     * @param currencyTo   The currency to convert to
     * @param amount       The amount to be converted
     * @return The converted amount
     */
    public static double convertCurrency(final BankOperationContext context,
                                         final Currency currencyFrom,
                                         final Currency currencyTo,
                                         final double amount) {
        return context.currencyExchangeService().convert(currencyFrom, currencyTo, amount);
    }

    /**
     * Remove funds from the bank account
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param amount      The amount to be removed
     */
    public static void removeFunds(final BankOperationContext context,
                                   final BankAccount bankAccount,
                                   final double amount) {
        context.bankAccService().removeFunds(bankAccount, amount);
    }

    /**
     * Add funds to the bank account
     *
     * @param context     The bank operation context
     * @param bankAccount The bank account
     * @param amount      The amount to be added
     */
    public static void addFunds(final BankOperationContext context,
                                final BankAccount bankAccount,
                                final double amount) {
        context.bankAccService().addFunds(bankAccount, amount);
    }

    /**
     * Calculate the cashback percentage for a transaction
     *
     * @param context     The bank operation context
     * @param merchant    The merchant
     * @param bankAccount The bank account
     * @param amount      The amount of the transaction
     * @param currency    The currency of the transaction
     * @return The cashback as a Discount
     */
    public static Discount calculateTransactionCashback(final BankOperationContext context,
                                                        final Merchant merchant,
                                                        final BankAccount bankAccount,
                                                        final double amount,
                                                        final Currency currency) {
        // Convert the amount to RON
        double convertedAmount = convertCurrency(context, currency, Currency.of("RON"), amount);
        return context.merchantService()
                .registerTransaction(merchant, bankAccount, convertedAmount);
    }

    /**
     * Validate the permissions of the user for the operation
     * This method is used only for business accounts
     *
     * @param context         The bank operation context
     * @param businessAccount The business account
     * @param userAccount     The user account
     * @param operation       The operation
     * @throws BankOperationException If the user does not have the required permissions
     */
    public static void validatePermissions(final BankOperationContext context,
                                           final BusinessAccount businessAccount,
                                           final UserAccount userAccount, final
                                           BusinessOperation operation) {
        if (!operation.validateUserPermission(businessAccount, userAccount)) {
            throw new BankOperationException(BankErrorType.PERMISSION_DENIED,
                    "You are not authorized to make this transaction");
        }
    }

}
