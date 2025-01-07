package org.poo.bank.splitPayment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.type.Currency;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class SplitPayment {
    private final int timestamp;
    // The accounts involved in the payment
    // Each account will have a corresponding amount in the amountPerAccount list
    // The amounts are in the currency specified in the currency field
    // Before creating a payment, make sure each account has enough funds
    private final List<BankAccount> involvedAccounts;
    private final List<Double> amountPerAccount;

    // The accounts that have confirmed the payment
    private final Set<BankAccount> confirmedAccounts = new HashSet<>();

    // These two fields purely informational, they are not used in the logic
    // They will only be used for logging purposes
    //private final double totalAmount;
    private final Currency currency;

    @Getter(AccessLevel.PACKAGE)
    private int confirmationsRemaining;
    private final SplitPaymentType type;

    @Builder
    private SplitPayment(final int timestamp, final List<BankAccount> involvedAccounts,
                         final List<Double> amountPerAccount,
                         final Currency currency, final SplitPaymentType type) {
        this.timestamp = timestamp;
        this.involvedAccounts = involvedAccounts;
        this.amountPerAccount = amountPerAccount;
        this.currency = currency;
        this.type = type;

        confirmationsRemaining = involvedAccounts.size();
    }

    void confirmPayment(final UserAccount userAccount) {
        // If a user is involved in the payment using multiple accounts, then all of them will be
        // confirmed
        involvedAccounts.stream()
                .filter(account -> account.getOwner() == userAccount)
                .forEach(confirmedAccounts::add);
    }

    boolean isPaymentConfirmed() {
        return confirmedAccounts.size() == involvedAccounts.size();
    }

    /**
     * Get the accounts involved in the payment.
     *
     * @return the accounts involved in the payment
     */
    public List<BankAccount> getAccountsInvolved() {
        return List.copyOf(involvedAccounts);
    }

    /**
     * Get the amount for each account involved in the payment.
     *
     * @return the amount for each account involved in the payment
     */
    public List<Double> getAmountPerAccount() {
        return List.copyOf(amountPerAccount);
    }
}
