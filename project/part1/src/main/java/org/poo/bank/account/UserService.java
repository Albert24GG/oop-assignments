package org.poo.bank.account;

import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UserService {
    /**
     * Mapping between the id (email) and the user account.
     */
    private final Map<String, UserAccount> users = new LinkedHashMap<>();

    /**
     * Create a new user account.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email of the user
     * @return the created user account
     * @throws IllegalArgumentException if the user already exists
     */
    public UserAccount createUser(final String firstName, final String lastName, final String email)
            throws IllegalArgumentException {
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User already exists");
        }

        final UserAccount user = new UserAccount(firstName, lastName, email);
        users.put(email, user);
        return user;
    }

    /**
     * Get the user account with the given email.
     *
     * @param email the email of the user
     * @return the user account with the given email, or {@code null} if the user does not exist
     */
    public UserAccount getUser(final String email) {
        return users.get(email);
    }

    /**
     * Remove the user account.
     *
     * @param user the user account to remove
     * @return the removed user account, or {@code null} if the user does not exist
     */
    public UserAccount removeUser(@NonNull final UserAccount user) {
        return users.remove(user.getId());
    }

    /**
     * Get all the user accounts.
     *
     * @return the list of user accounts
     */
    public List<UserAccount> getUsers() {
        return List.copyOf(users.values());
    }
}
