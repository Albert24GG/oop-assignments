package org.poo.bank.account;

import lombok.Builder;
import lombok.Getter;
import org.poo.bank.type.Email;

import java.util.List;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public final class UserView {
    private String firstName;
    private String lastName;
    private Email email;
    private List<BankAccView> accounts;

    /**
     * Creates a new user view from a user account
     *
     * @param user the user account to create the view from
     * @return the new user view
     */
    public static UserView from(final UserAccount user) {
        return UserView.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .accounts(user.getAccounts().stream()
                        .map(BankAccView::from)
                        .toList())
                .build();
    }
}
