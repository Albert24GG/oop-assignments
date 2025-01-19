package org.poo.bank.account;

import lombok.Getter;
import org.poo.bank.type.Currency;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BusinessAccount extends BankAccount {
    private final Map<UserAccount, BusinessAccountRole> accountMembers = new LinkedHashMap<>();
    private final Map<BusinessAccountRole, AccountRoleRestrictions> roleRestrictions = Map.of(
            BusinessAccountRole.OWNER, new AccountRoleRestrictions(BusinessAccountRole.OWNER),
            BusinessAccountRole.MANAGER, new AccountRoleRestrictions(BusinessAccountRole.MANAGER),
            BusinessAccountRole.EMPLOYEE, new AccountRoleRestrictions(BusinessAccountRole.EMPLOYEE)
    );

    class AccountRoleRestrictions {
        private Double spendingLimit = null;
        private Double depositLimit = null;
        @Getter
        private List<BusinessAccountPermission> permissions;

        AccountRoleRestrictions(final BusinessAccountRole role) {
            // set the default permissions for the role
            this.permissions = role.getPermissions();
        }

        /**
         * Get the spending limit for the role
         *
         * @return an {@link Optional} containing the spending limit for the role, or an
         * {@link Optional#empty()} if the spending limit is not set
         */
        public Optional<Double> getSpendingLimit() {
            return Optional.ofNullable(spendingLimit);
        }

        /**
         * Get the deposit limit for the role
         *
         * @return an {@link Optional} containing the deposit limit for the role, or an
         * {@link Optional#empty()} if the deposit limit is not set
         */
        public Optional<Double> getDepositLimit() {
            return Optional.ofNullable(depositLimit);
        }
    }


    public BusinessAccount(final UserAccount owner, final Currency currency) {
        super(BankAccountType.BUSINESS, currency, owner);

        accountMembers.put(owner, BusinessAccountRole.OWNER);
    }

    /**
     * Add a member to the account
     *
     * @param user the user to add
     * @param role the role of the user
     */
    public void addMemberAccount(final UserAccount user, final BusinessAccountRole role) {
        accountMembers.put(user, role);
    }

    /**
     * Get the members of the account
     *
     * @return a list of the members of the account
     */
    public List<UserAccount> getAccountMembers() {
        return accountMembers.keySet().stream().toList();
    }

    void setSpendingLimit(final BusinessAccountRole role, final double spendingLimit) {
        if (spendingLimit <= 0) {
            throw new IllegalArgumentException("Spending limit must be positive");
        }
        roleRestrictions.get(role).spendingLimit = spendingLimit;
    }

    void setDepositLimit(final BusinessAccountRole role, final double depositLimit) {
        if (depositLimit <= 0) {
            throw new IllegalArgumentException("Deposit limit must be positive");
        }
        roleRestrictions.get(role).depositLimit = depositLimit;
    }

    /**
     * Get the permissions of a member of the account
     *
     * @param user the user in question
     * @return an {@link Optional} containing the permissions of the user, or an
     * {@link Optional#empty()} if the user is not a member of the account
     */
    public Optional<List<BusinessAccountPermission>> getMemberPermissions(final UserAccount user) {
        return Optional.ofNullable(accountMembers.get(user).getPermissions());
    }

    /**
     * Get the role of a member of the account
     *
     * @param user the user in question
     * @return an {@link Optional} containing the role of the user, or an
     * {@link Optional#empty()} if the user is not a member of the account
     */
    public Optional<BusinessAccountRole> getRole(final UserAccount user) {
        return Optional.ofNullable(accountMembers.get(user));
    }

    AccountRoleRestrictions getRoleRestrictions(final BusinessAccountRole role) {
        return roleRestrictions.get(role);
    }

    /**
     * These two are mostly convenience methods since we only use the spending and deposit limits
     * for employees in the current implementation. But in a generic implementation, we would
     * have a method that takes a role as a parameter and returns the corresponding spending and
     * deposit limits.
     */

    /**
     * Get the spending limit for employees
     *
     * @return an {@link Optional} containing the spending limit for employees, or an
     * {@link Optional#empty()} if the spending limit is not set
     */
    public Optional<Double> getEmployeeSpendingLimit() {
        return roleRestrictions.get(BusinessAccountRole.EMPLOYEE).getSpendingLimit();
    }

    /**
     * Get the deposit limit for employees
     *
     * @return an {@link Optional} containing the deposit limit for employees, or an
     * {@link Optional#empty()} if the deposit limit is not set
     */
    public Optional<Double> getEmployeeDepositLimit() {
        return roleRestrictions.get(BusinessAccountRole.EMPLOYEE).getDepositLimit();
    }
}
