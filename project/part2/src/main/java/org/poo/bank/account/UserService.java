package org.poo.bank.account;

import org.poo.bank.type.Date;
import org.poo.bank.type.Email;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class UserService {
    private final Map<Email, UserAccount> users = new LinkedHashMap<>();

    /**
     * Create a new user account.
     *
     * @param firstName  the first name of the user
     * @param lastName   the last name of the user
     * @param email      the email of the user
     * @param birthDate  the birthdate of the user
     * @param occupation the occupation of the user
     * @return the created user account
     * @throws IllegalArgumentException if the user already exists
     */
    public UserAccount createUser(final String firstName, final String lastName, final Email email,
                                  final
                                  Date birthDate, final String occupation)
            throws IllegalArgumentException {
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User already exists");
        }

        final UserAccount user = new UserAccount(firstName, lastName, email, birthDate, occupation);
        users.put(email, user);
        return user;
    }

    /**
     * Get the user account with the given email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the user account with the given email, or an
     * {@link Optional#empty()} if the user does not exist
     */
    public Optional<UserAccount> getUser(final Email email) {
        return Optional.ofNullable(users.get(email));
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
