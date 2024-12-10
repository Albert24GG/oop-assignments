package org.poo.bank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.Bank;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class UserAccount {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<BankAccount> accounts = new ArrayList<>();

    /**
     * Return the id of the user account.
     * Currently, the id is the email.
     *
     * @return the id of the user account
     */
    public String getId() {
        return email;
    }

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
     * @return the removed account, or {@code null} if the account does not exist
     */
    BankAccount removeAccount(final BankAccount account) {
        return accounts.remove(account) ? account : null;
    }
}
