package org.poo.bank.account;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.type.Date;
import org.poo.bank.type.Email;
import org.poo.bank.servicePlan.ServicePlan;
import org.poo.bank.servicePlan.ServicePlanType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class UserAccount {
    @Getter
    @EqualsAndHashCode.Include
    private final String firstName;
    @Getter
    @EqualsAndHashCode.Include
    private final String lastName;
    @Getter
    @EqualsAndHashCode.Include
    private final Email email;
    private final List<BankAccount> accounts = new ArrayList<>();
    @Getter
    @EqualsAndHashCode.Include
    private final Date birthDate;
    private final String occupation;
    @Getter
    private ServicePlan servicePlan;

    /**
     * Add a bank account to the user account.
     *
     * @param account the bank account to add
     * @throws IllegalArgumentException if the account does not belong to the user
     */
    void addAccount(final BankAccount account) {
        if (account.getOwner() != this) {
            throw new IllegalArgumentException("The account does not belong to the user");
        }
        accounts.add(account);
    }

    /**
     * Remove a bank account from the user account.
     *
     * @param account the bank account to remove
     * @return an {@link Optional} containing the removed bank account, or an
     * {@link Optional#empty()} if the account does not exist
     */
    Optional<BankAccount> removeAccount(final BankAccount account) {
        return Optional.ofNullable(accounts.remove(account) ? account : null);
    }

    /**
     * Get the list of bank accounts.
     *
     * @return the list of bank accounts
     */
    public List<BankAccount> getAccounts() {
        return List.copyOf(accounts);
    }

    void upgradeServicePlan(final ServicePlanType newPlan) {
        servicePlan = servicePlan.upgradePlan(newPlan);
    }
}
