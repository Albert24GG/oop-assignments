package org.poo.bank.account;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
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
}
