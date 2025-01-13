package org.poo.bank.account;

import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public BankAccount createAccount(final UserAccount owner,
                                     final Currency currency,
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
     * @return an {@link Optional} containing the account with the given IBAN, or an
     * {@link Optional#empty()} if the account does not exist
     */
    public Optional<BankAccount> getAccountByIban(final IBAN iban) {
        return Optional.ofNullable(ibanMapping.get(iban));
    }

    /**
     * Get the account with the given alias.
     *
     * @param alias the alias of the account
     * @return an {@link Optional} containing the account with the given alias, or an
     * {@link Optional#empty()} if the account does not exist
     */
    public Optional<BankAccount> getAccountByAlias(final String alias) {
        return Optional.ofNullable(aliasMapping.get(alias));
    }


    /**
     * Remove the account.
     *
     * @param account the account to remove
     * @return an {@link Optional} containing the removed account, or an {@link Optional#empty()}
     * if the account does not exist
     */
    public Optional<BankAccount> removeAccount(final BankAccount account) {
        account.getOwner().removeAccount(account);
        aliasMapping.remove(account.getAlias());
        return Optional.ofNullable(ibanMapping.remove(account.getIban()));
    }

    /**
     * Add funds to the given account.
     *
     * @param account the account
     * @param amount  the amount to add
     */
    public void addFunds(final BankAccount account, final double amount) {
        account.addFunds(amount);
    }

    /**
     * Remove funds from the given account.
     *
     * @param account the account
     * @param amount  the amount to remove
     */
    public void removeFunds(final BankAccount account, final double amount) {
        account.removeFunds(amount);
    }

    /**
     * Set the minimum balance for the given account.
     *
     * @param account    the account
     * @param minBalance the minimum balance
     */
    public void setMinBalance(final BankAccount account, final double minBalance) {
        account.setMinBalance(minBalance);
    }

    /**
     * Change the interest rate of the given savings account.
     *
     * @param account      the savings account
     * @param interestRate the new interest rate
     */
    public void changeInterestRate(final SavingsAccount account, final double interestRate) {
        account.changeInterestRate(interestRate);
    }

    /**
     * Collect interest for the given savings account.
     *
     * @param account the savings account
     */
    public void collectInterest(final SavingsAccount account) {
        account.collectInterest();
    }

    /**
     * Validate that the account is owned by the given user.
     *
     * @param account the account
     * @param user    the user
     * @return {@code true} if the account is owned by the user, {@code false} otherwise
     */
    public boolean validateAccountOwnership(final BankAccount account, final UserAccount user) {
        return account.getOwner().equals(user);
    }

    /**
     * Check if the account can be deleted.
     *
     * @param account the account
     * @return {@code true} if the account can be deleted, {@code false} otherwise
     */
    public boolean canDeleteAccount(final BankAccount account) {
        return account.getBalance() == 0;
    }

    /**
     * Validate that the account has enough funds.
     *
     * @param sender the account
     * @param amount the amount
     * @return {@code true} if the account has enough funds, {@code false} otherwise
     */
    public boolean validateFunds(final BankAccount sender, final double amount) {
        return sender.getBalance() >= amount;
    }

    /**
     * Get the service plan of the given account.
     *
     * @param account the account
     * @return the service plan
     */
    public ServicePlan getServicePlan(final BankAccount account) {
        return account.getOwner().getServicePlan();
    }

    /**
     * Add a member to the business account.
     *
     * @param account the business account
     * @param user    the user to add
     * @param role    the role of the user
     * @throws IllegalArgumentException if the role is {@link BusinessAccountRole#OWNER}
     */
    public void addBusinessAccountMember(final BusinessAccount account, final UserAccount user,
                                         final BusinessAccountRole role) {
        if (role == BusinessAccountRole.OWNER) {
            throw new IllegalArgumentException("Cannot add an owner");
        }
        account.addMemberAccount(user, role);
    }

    /**
     * Set the spending limit for the given business account role.
     *
     * @param account       the business account
     * @param role          the role
     * @param spendingLimit the spending limit
     */
    public void setBusinessAccountSpendingLimit(final BusinessAccount account,
                                                final BusinessAccountRole role,
                                                final double spendingLimit) {
        account.setSpendingLimit(role, spendingLimit);
    }

    /**
     * Set the deposit limit for the given business account role.
     *
     * @param account      the business account
     * @param role         the role
     * @param depositLimit the deposit limit
     */
    public void setBusinessAccountDepositLimit(final BusinessAccount account,
                                               final BusinessAccountRole role,
                                               final double depositLimit) {
        account.setDepositLimit(role, depositLimit);
    }
}
