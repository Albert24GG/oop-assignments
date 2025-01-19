package org.poo.bank.log.interfaces;

import org.poo.bank.merchant.Merchant;

import java.util.Optional;

/**
 * Interface implemented by logs that are related to a transaction.
 */
public interface TransactionLog extends UserTransactionLog {

    /**
     * Get the merchant account that received the transaction.
     *
     * @return an {@link Optional} containing the merchant account that received the transaction,
     * or an empty {@link Optional} if the merchant account is not available
     */
    Optional<Merchant> getRecipientMerchant();

    /**
     * Get the amount of the transaction.
     * The currency of the transaction is the same as the currency of the account.
     *
     * @return the amount of the transaction
     */
    double getAmount();
}
