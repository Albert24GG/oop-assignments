package org.poo.bank.merchant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PercentageDiscount implements Discount {
    // Percentage of the discount (0.0 to 1.0)
    private final double percentage;

    @Override
    public double apply(final double amount) {
        return amount * percentage;
    }
}
