package org.poo.bank.merchant;

import lombok.NonNull;

public enum MerchantType {
    FOOD,
    CLOTHES,
    TECH;

    /**
     * Returns the MerchantType enum value corresponding to the given string.
     *
     * @param type the string to be converted to a MerchantType enum value
     * @return the MerchantType enum value corresponding to the given string
     * or null if the string does not correspond to any MerchantType enum value
     */
    public static MerchantType of(@NonNull final String type) {
        try {
            return MerchantType.valueOf(type.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase().replaceFirst("^.", String.valueOf(name().charAt(0)));
    }
}
