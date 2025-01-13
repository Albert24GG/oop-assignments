package org.poo.bank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the roles that a user can have in a business account
 * along with the default permissions for each role
 */
@RequiredArgsConstructor
@Getter
public enum BusinessAccountRole {
    OWNER(Arrays.stream(BusinessAccountPermission.values()).toList()),
    MANAGER(List.of(BusinessAccountPermission.DEPOSIT,
            BusinessAccountPermission.TRANSFER,
            BusinessAccountPermission.CARD_PAYMENT,
            BusinessAccountPermission.CARD_CREATION,
            BusinessAccountPermission.CARD_DELETION_SAME_OWNER,
            BusinessAccountPermission.CARD_DELETION_DIFFERENT_OWNER)),
    EMPLOYEE(List.of(BusinessAccountPermission.DEPOSIT,
            BusinessAccountPermission.TRANSFER,
            BusinessAccountPermission.CARD_PAYMENT,
            BusinessAccountPermission.CARD_CREATION,
            BusinessAccountPermission.CARD_DELETION_SAME_OWNER));

    private final List<BusinessAccountPermission> permissions;

    /**
     * Gets the business account role from a string
     *
     * @param role the string to convert
     * @return the business account role
     * @throws IllegalArgumentException if the role is invalid
     */
    public static BusinessAccountRole of(final String role) {
        try {
            return BusinessAccountRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid business account role: " + role);
        }
    }
}
