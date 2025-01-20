package org.poo.bank.account;


import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;

/**
 * Represents an operation that requires permissions when performed on a business account
 */
public abstract class BusinessOperation {
    /**
     * Get the permissions required to perform this operation
     *
     * @return a list of permissions required to perform this operation
     */
    protected abstract List<BusinessAccountPermission> getRequiredPermissions();

    /**
     * Check if the user has the required permissions to perform this operation
     *
     * @param account the account on which the operation is performed
     * @param user    the user performing the operation
     * @return {@code true} if the user has the required permissions, {@code false} otherwise
     */
    public boolean validateUserPermission(final BusinessAccount account, final UserAccount user) {
        return new HashSet<>(account.getRole(user)
                .map(account::getRoleRestrictions)
                .map(BusinessAccount.AccountRoleRestrictions::getPermissions)
                .orElse(List.of()))
                .containsAll(getRequiredPermissions());
    }

    @RequiredArgsConstructor
    public static final class AddFunds extends BusinessOperation {
        private final double amount;

        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.DEPOSIT);
        }

        @Override
        public boolean validateUserPermission(final BusinessAccount account,
                                              final UserAccount user) {
            // Check if the deposit amount is within the user's deposit limit
            if (account.getRole(user).map(account::getRoleRestrictions)
                    .map(BusinessAccount.AccountRoleRestrictions::getDepositLimit)
                    .filter(limit -> amount <= limit.orElse(Double.MAX_VALUE))
                    .isEmpty()) {
                return false;
            }

            return super.validateUserPermission(account, user);
        }
    }

    public static final class AddCard extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.CARD_CREATION);
        }
    }


    public static final class RemoveCardSameOwner extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.CARD_DELETION_SAME_OWNER);
        }
    }

    public static final class RemoveCardDifferentOwner extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.CARD_DELETION_DIFFERENT_OWNER);
        }
    }

    public static final class SetSpendingLimit extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.SET_SPENDING_LIMIT);
        }
    }

    public static final class SetDepositLimit extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.SET_DEPOSIT_LIMIT);
        }
    }

    public static final class SetMinimumBalance extends BusinessOperation {
        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.SET_MINIMUM_BALANCE);
        }
    }

    private static boolean checkSpendingLimit(final BusinessAccount account, final UserAccount user,
                                              final double amount) {
        return account.getRole(user)
                .map(account::getRoleRestrictions)
                .map(BusinessAccount.AccountRoleRestrictions::getSpendingLimit)
                .filter(limit -> amount <= limit.orElse(Double.MAX_VALUE))
                .isPresent();
    }

    @RequiredArgsConstructor
    public static final class CardPayment extends BusinessOperation {
        private final double amount;

        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.CARD_PAYMENT);
        }

        @Override
        public boolean validateUserPermission(final BusinessAccount account,
                                              final UserAccount user) {
            // Check if the payment amount is within the user's spending limit
            if (!checkSpendingLimit(account, user, amount)) {
                return false;
            }

            return super.validateUserPermission(account, user);
        }
    }

    @RequiredArgsConstructor
    public static final class Transfer extends BusinessOperation {
        private final double amount;

        @Override
        protected List<BusinessAccountPermission> getRequiredPermissions() {
            return List.of(BusinessAccountPermission.TRANSFER);
        }

        @Override
        public boolean validateUserPermission(final BusinessAccount account,
                                              final UserAccount user) {
            // Check if the transfer amount is within the user's spending limit
            if (!checkSpendingLimit(account, user, amount)) {
                return false;
            }
            return super.validateUserPermission(account, user);
        }
    }

}
