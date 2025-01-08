package org.poo.bank.merchant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
abstract class Cashback {
    @Getter
    private final double percentage;
    // The type of merchant that the discount is applicable for (if any)
    // If the discount is applicable for all merchants, this field should be empty
    @Getter(AccessLevel.PACKAGE)
    private final MerchantType applicableType;

    /**
     * Check if the discount is applicable for the same transaction.
     * This implies that the discount is meant to be applied either on the same transaction or on
     * future transactions.
     *
     * @return true if the discount is applicable, false otherwise
     */
    abstract boolean isApplicableNow();

    /**
     * Check if the discount is applicable only once.
     *
     * @return true if the discount is applicable only once, false otherwise
     */
    abstract boolean isApplicableOneTime();

    /**
     * Check if the discount is applicable for the given merchant.
     *
     * @param merchant the merchant to check
     * @return true if the discount is applicable, false otherwise
     */
    final boolean isApplicableFor(final Merchant merchant) {
        return Optional.ofNullable(applicableType).map(type -> type == merchant.getType())
                .orElse(true);
    }
}
