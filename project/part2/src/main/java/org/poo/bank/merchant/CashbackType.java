package org.poo.bank.merchant;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum CashbackType {
    TRANSACTION_BASED(Merchant.TransactionBasedCashback::create),
    SPENDING_BASED(Merchant.SpendingBasedCashback::create);

    private final Function<Merchant, CashbackStrategy> strategyClass;

    /**
     * Creates a new instance of the strategy class.
     *
     * @return a new instance of the strategy class
     */
    public CashbackStrategy createStrategy(final Merchant merchant) {
        try {
            return strategyClass.apply(merchant);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the cashback type from the given string.
     *
     * @param type the type string
     * @return the cashback type
     */
    public static CashbackType of(@NonNull final String type) {
        return switch (type) {
            case "nrOfTransactions" -> TRANSACTION_BASED;
            case "spendingThreshold" -> SPENDING_BASED;
            default -> throw new IllegalArgumentException("Invalid cashback type");
        };
    }
}
