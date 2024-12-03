package org.poo.bank.account;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class BankAccService {
    /**
     * Mapping between the IBAN and the bank account.
     */
    private final Map<String, BankAccount> accounts = new HashMap<>();

    /**
     * Create a new bank account.
     *
     * @param owner        the owner of the account
     * @param currency     the currency of the account
     * @param type         the type of the account
     * @param interestRate the interest rate of the account
     * @return the created account
     */
    public BankAccount createAccount(final UserAccount owner, final String currency,
                                     final BankAccount.Type type, final double interestRate) {
        BankAccount account = BankAccount.createAccount(type, owner, currency, interestRate);
        accounts.put(account.getIban(), account);
        return account;
    }

    /**
     * Register an alias for the given account.
     *
     * @param account the account
     * @param alias   the alias
     */
    public void registerAlias(final BankAccount account, final String alias) {
        accounts.put(alias, account);
        account.setAlias(alias);
    }

    /**
     * Get the account with the given IBAN or alias.
     *
     * @param identifier the IBAN or alias of the account
     * @return the account with the given identifier, or {@code null} if no account is found
     */
    public BankAccount getAccount(final String identifier) {
        return accounts.get(identifier);
    }

    /**
     * Remove the account.
     *
     * @param account the account to remove
     * @return the removed account, or {@code null} if the account does not exist
     */
    public BankAccount removeAccount(@NonNull final BankAccount account) {
        accounts.remove(account.getAlias());
        return accounts.remove(account.getIban());
    }

    /**
     * Add funds to the given account.
     *
     * @param account the account
     * @param amount  the amount to add
     */
    public void addFunds(@NonNull final BankAccount account, final double amount) {
        account.addFunds(amount);
    }

    /**
     * Remove funds from the given account.
     *
     * @param account the account
     * @param amount  the amount to remove
     */
    public void removeFunds(@NonNull final BankAccount account, final double amount) {
        account.removeFunds(amount);
    }

    /**
     * Set the minimum balance for the given account.
     *
     * @param account    the account
     * @param minBalance the minimum balance
     */
    public void setMinBalance(@NonNull final BankAccount account, final double minBalance) {
        account.setMinBalance(minBalance);
    }

    /**
     * Change the interest rate of the given account.
     * This operation is only supported for savings accounts.
     *
     * @param account      the account
     * @param interestRate the new interest rate
     */
    public void changeInterestRate(@NonNull final BankAccount account, final double interestRate) {
        account.changeInterestRate(interestRate);
    }

    /**
     * Collect interest for the given account.
     * This operation is only supported for savings accounts.
     *
     * @param account the account
     */
    public void collectInterest(@NonNull final BankAccount account) {
        account.collectInterest();
    }

}
