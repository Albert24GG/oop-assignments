package org.poo.bank.merchant;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
abstract class Discount {
    @Getter
    private final double percentage;
    // The type of merchant that the discount is applicable for (if any)
    // If the discount is applicable for all merchants, this field should be empty
    @Getter(AccessLevel.PACKAGE)
    private final Optional<MerchantType> applicableType;

    /**
     * Check if the discount is applicable for the same transaction.
     * This implies that the discount is meant to be applied either on the same transaction or on
     * future transactions.
     *
     * @return true if the discount is applicable, false otherwise
     */
    public abstract boolean isApplicableNow();

    /**
     * Check if the discount is applicable only once.
     *
     * @return true if the discount is applicable only once, false otherwise
     */
    public abstract boolean isApplicableOneTime();

    /**
     * Check if the discount is applicable for the given merchant.
     *
     * @param merchant the merchant to check
     * @return true if the discount is applicable, false otherwise
     */
    public final boolean isApplicableFor(final Merchant merchant) {
        return applicableType.map(type -> type == merchant.getType()).orElse(true);
    }
}
