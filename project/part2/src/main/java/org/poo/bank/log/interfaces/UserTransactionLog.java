package org.poo.bank.log.interfaces;

import org.poo.bank.account.UserAccount;

/**
 * Interface implemented by logs that are related to a user transaction.
 */
public interface UserTransactionLog {
    /**
     * Get the user account that initiated the transaction.
     * @return the user account that initiated the transaction
     */
    UserAccount getUserAccount();

    /**
     * Get the amount of the transaction.
     * The currency of the transaction is the same as the currency of the account.
     *
     * @return the amount of the transaction
     */
    double getAmount();
}
