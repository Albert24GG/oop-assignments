package org.poo.bank;

import org.poo.bank.account.BankAccService;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.account.UserService;
import org.poo.bank.account.UserView;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardService;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionService;
import org.poo.bank.transaction.impl.AccountCreationLog;
import org.poo.bank.transaction.impl.CardOpLog;
import org.poo.bank.transaction.impl.GenericLog;

import java.util.List;

public final class Bank {
    private final CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();
    private final CardService cardService = new CardService();
    private final UserService userService = new UserService();
    private final BankAccService bankAccService = new BankAccService();
    private final TransactionService transactionService = new TransactionService();

    /**
     * Get a list of views of the users.
     *
     * @return the list of users
     */
    public List<UserView> getUsers() {
        return userService.getUsers().stream().map(UserView::from).toList();
    }

    /**
     * Create a new user.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email of the user
     */
    public void createUser(final String firstName, final String lastName, final String email) {
        userService.createUser(firstName, lastName, email);
    }

}
