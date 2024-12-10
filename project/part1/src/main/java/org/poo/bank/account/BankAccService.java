package org.poo.bank.account;

import lombok.NonNull;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.HashMap;
import java.util.Map;

public final class BankAccService {
    private final Map<IBAN, BankAccount> ibanMapping = new HashMap<>();
    private final Map<String, BankAccount> aliasMapping = new HashMap<>();

    /**
     * Create a new bank account.
     *
     * @param owner        the owner of the account
     * @param currency     the currency of the account
     * @param type         the type of the account
     * @param interestRate the interest rate of the account
     * @return the created account
     */
    public BankAccount createAccount(@NonNull final UserAccount owner,
                                     @NonNull final Currency currency,
                                     final BankAccountType type, final double interestRate) {
        BankAccount account = BankAccount.createAccount(type, owner, currency, interestRate);
        ibanMapping.put(account.getIban(), account);
        return account;
    }

    /**
     * Register an alias for the given account.
     *
     * @param account the account
     * @param alias   the alias
     */
    public void registerAlias(final BankAccount account, final String alias) {
        aliasMapping.put(alias, account);
        account.setAlias(alias);
    }

    /**
     * Get the account with the given IBAN.
     *
     * @param iban the IBAN of the account
     * @return the account with the given IBAN, or {@code null} if the account does not exist
     */
    public BankAccount getAccountByIban(@NonNull final IBAN iban) {
        return ibanMapping.get(iban);
    }

    /**
     * Get the account with the given alias.
     *
     * @param alias the alias of the account
     * @return the account with the given alias, or {@code null} if the account does not exist
     */
    public BankAccount getAccountByAlias(@NonNull final String alias) {
        return aliasMapping.get(alias);
    }


    /**
     * Remove the account.
     *
     * @param account the account to remove
     * @return the removed account, or {@code null} if the account does not exist
     */
    public BankAccount removeAccount(@NonNull final BankAccount account) {
        account.getOwner().removeAccount(account);
        aliasMapping.remove(account.getAlias());
        return ibanMapping.remove(account.getIban());
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
