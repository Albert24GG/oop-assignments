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

    /**
     * Create a new bank account.
     *
     * @param ownerEmail   the email of the owner
     * @param currency     the currency of the account
     * @param type         the type of the account. Valid values are:
     *                     <ul>
     *                     <li>SAVINGS</li>
     *                     <li>CLASSIC</li>
     *                     </ul>
     * @param interestRate the interest rate of the account
     * @param timestamp    the timestamp of the account creation
     * @throws IllegalArgumentException if the account type is invalid
     */
    public void createAccount(final String ownerEmail, final String currency,
                              final String type, final double interestRate,
                              int timestamp) {
        BankAccount.Type accountType = BankAccount.Type.fromString(type);

        if (accountType == null) {
            throw new IllegalArgumentException("Invalid account type");
        }

        BankAccount account =
                bankAccService.createAccount(userService.getUser(ownerEmail), currency, accountType,
                        interestRate);

        TransactionLog transactionLog = AccountCreationLog.builder()
                .timestamp(timestamp)
                .description("New account created")
                .build();
        transactionService.logTransaction(account.getIban(), transactionLog);
    }

    /**
     * Create a new card linked to the given account.
     *
     * @param ownerEmail  the email of the owner
     * @param accountIban the IBAN of the account
     * @param type        the type of the card. Valid values are:
     *                    <ul>
     *                    <li>DEBIT</li>
     *                    <li>SINGLE_USE</li>
     *                    </ul>
     * @param timestamp   the timestamp of the card creation
     * @throws IllegalArgumentException if any of the following conditions is met:
     *                                  <ul/
     *                                  <li>the user/bank account does not exist</li>
     *                                  <li>the card type is invalid</li>
     *                                  </ul>
     */
    public void createCard(final String ownerEmail, final String accountIban, final String type,
                           final int timestamp) {
        UserAccount userAccount = userService.getUser(ownerEmail);
        BankAccount bankAccount = bankAccService.getAccount(accountIban);
        Card.Type cardType = Card.Type.fromString(type);

        if (userAccount == null || bankAccount == null) {
            throw new IllegalArgumentException("User or bank account not found");
        }
        if (cardType == null) {
            throw new IllegalArgumentException("Invalid card type");
        }

        TransactionLog transactionLog;

        // If the user is not the owner of the account, the card creation fails and an error is
        // logged
        if (userAccount != bankAccount.getOwner()) {
            transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .error("Card creation failed")
                    .build();
        } else {
            Card newCard = cardService.createCard(bankAccount, cardType);
            transactionLog = CardOpLog.builder()
                    .timestamp(timestamp)
                    .card(newCard.getNumber())
                    .cardHolder(userAccount.getEmail())
                    .account(bankAccount.getIban())
                    .build();
        }

        transactionService.logTransaction(accountIban, transactionLog);
    }

    /**
     * Add funds to the given account.
     *
     * @param accountIban the IBAN of the account
     * @param amount      the amount to add
     * @param timestamp   the timestamp of the operation
     * @throws IllegalArgumentException if the account does not exist
     */
    public void addFunds(final String accountIban, final double amount, final int timestamp) {
        BankAccount account = bankAccService.getAccount(accountIban);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        bankAccService.addFunds(account, amount);
    }

    /**
     * Delete a specific account of a user.
     *
     * @param ownerEmail  the email of the owner
     * @param accountIban the IBAN of the account
     * @param timestamp   the timestamp of the operation
     * @throws IllegalArgumentException if one of the following conditions is met:
     *                                  <ul>
     *                                      <li>the user/bank account does not exist</li>
     *                                      <li>the user is not the owner of the account</li>
     *                                      <li>the account has funds remaining</li>
     *                                  </ul>
     */
    public void deleteAccount(final String ownerEmail, final String accountIban,
                              final int timestamp) {
        UserAccount userAccount = userService.getUser(ownerEmail);
        BankAccount bankAccount = bankAccService.getAccount(accountIban);
        if (userAccount == null || bankAccount == null) {
            throw new IllegalArgumentException("User or bank account not found");
        }
        if (userAccount != bankAccount.getOwner()) {
            throw new IllegalArgumentException("User is not the owner of the account");
        }
        if (bankAccount.getBalance() != 0) {
            TransactionLog transactionLog = GenericLog.builder()
                    .timestamp(timestamp)
                    .error("Account couldn't be deleted - there are funds remaining")
                    .build();
            transactionService.logTransaction(accountIban, transactionLog);
            throw new IllegalArgumentException(
                    "Account couldn't be deleted - see org.poo.transactions for details");
        }

        TransactionLog transactionLog = GenericLog.builder()
                .timestamp(timestamp)
                .description("Account deleted")
                .build();
        transactionService.logTransaction(accountIban, transactionLog);
        // Remove the account and its cards
        bankAccService.removeAccount(bankAccount);
        bankAccount.getCards().forEach(cardService::removeCard);
    }

    /**
     * Delete a specific card.
     *
     * @param cardNumber the number of the card
     * @param timestamp  the timestamp of the operation
     * @throws IllegalArgumentException if the card does not exist
     */
    public void deleteCard(final String cardNumber, final int timestamp) {
        Card card = cardService.getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException("Card not found");
        }
        cardService.removeCard(card);
        BankAccount linkedAccount = card.getLinkedAccount();

        TransactionLog transactionLog = CardOpLog.builder()
                .timestamp(timestamp)
                .card(cardNumber)
                .cardHolder(linkedAccount.getOwner().getEmail())
                .account(linkedAccount.getIban())
                .description("Card deleted")
                .build();
        transactionService.logTransaction(linkedAccount.getIban(), transactionLog);
    }
}
