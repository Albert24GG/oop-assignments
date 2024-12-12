package org.poo.bank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.type.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserAccount {
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final Email email;
    private final List<BankAccount> accounts = new ArrayList<>();

    /**
     * Add a bank account to the user account.
     *
     * @param account the bank account to add
     * @throws IllegalArgumentException if the account does not belong to the user
     */
    void addAccount(final BankAccount account) {
        if (account.getOwner() != this) {
            throw new IllegalArgumentException("The account does not belong to the user");
        }
        accounts.add(account);
    }

    /**
     * Remove a bank account from the user account.
     *
     * @param account the bank account to remove
     * @return an {@link Optional} containing the removed bank account, or an
     * {@link Optional#empty()} if the account does not exist
     */
    Optional<BankAccount> removeAccount(final BankAccount account) {
        return Optional.ofNullable(accounts.remove(account) ? account : null);
    }

    /**
     * Get the list of bank accounts.
     *
     * @return the list of bank accounts
     */
    public List<BankAccount> getAccounts() {
        return List.copyOf(accounts);
    }
}
