package org.poo.bank.merchant;

import org.poo.bank.account.BankAccount;

import java.util.Optional;

interface CashbackStrategy {
    /**
     * Registers a transaction made by a bank account to a merchant.
     *
     * @param bankAccount the bank account of the user that made the transaction
     * @param amount      the amount of the transaction in RON
     * @return an {@link Optional} containing the discount that the user received, or an empty
     * {@link Optional} if the user did not receive any discount
     */
    Optional<Discount> registerTransaction(BankAccount bankAccount,
                                           double amount);
}
