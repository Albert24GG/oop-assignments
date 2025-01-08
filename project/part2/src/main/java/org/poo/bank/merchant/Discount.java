package org.poo.bank.merchant;

/**
 * Represents a discount.
 * It can be either a fixed amount or a percentage.
 */
public interface Discount {
    /**
     * Returns the discount amount.
     *
     * @param amount the amount to apply the discount to
     * @return the discount amount in the same currency as the input amount
     */
    double apply(double amount);
}
