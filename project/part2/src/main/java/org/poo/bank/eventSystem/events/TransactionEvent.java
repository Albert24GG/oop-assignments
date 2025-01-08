package org.poo.bank.eventSystem.events;

import lombok.Getter;
import org.poo.bank.account.BankAccount;
import org.poo.bank.merchant.Merchant;
import org.poo.bank.type.Currency;

@Getter
public final class TransactionEvent {
    private final BankAccount senderBankAccount;

    // The receiver can be either a user or a merchant
    private final BankAccount receiverBankAccount;
    private final Merchant merchant;

    // The amount of the transaction in the specified currency
    private final double amount;
    private final Currency currency;

    /**
     * Creates a new transaction event between a user and a user.
     *
     * @param senderBankAccount   the user that sends the money
     * @param receiverBankAccount the user that receives the money
     * @param amount              the amount of the transaction
     * @param currency            the currency of the transaction
     */
    public TransactionEvent(final BankAccount senderBankAccount,
                            final BankAccount receiverBankAccount,
                            final double amount,
                            final Currency currency) {
        this.senderBankAccount = senderBankAccount;
        this.receiverBankAccount = receiverBankAccount;
        this.merchant = null;
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Creates a new transaction event between a user and a merchant.
     *
     * @param senderBankAccount the user that sends the money
     * @param merchant          the merchant that receives the money
     * @param amount            the amount of the transaction
     * @param currency          the currency of the transaction
     */
    public TransactionEvent(final BankAccount senderBankAccount, final Merchant merchant,
                            final double amount,
                            final Currency currency) {
        this.senderBankAccount = senderBankAccount;
        this.receiverBankAccount = null;
        this.merchant = merchant;
        this.amount = amount;
        this.currency = currency;
    }
}
