package org.poo.bank.merchant;

import org.poo.bank.account.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class CashbackService {
    private final Map<BankAccount, List<Cashback>> pendingDiscounts = new HashMap<>();
    private final Map<BankAccount, List<Cashback>> appliedDiscounts = new HashMap<>();

    private List<Cashback> getPendingDiscounts(final BankAccount bankAccount) {
        return pendingDiscounts.computeIfAbsent(bankAccount, k -> new ArrayList<>());
    }

    private List<Cashback> getAppliedDiscounts(final BankAccount bankAccount) {
        return appliedDiscounts.computeIfAbsent(bankAccount, k -> new ArrayList<>());
    }

    Discount registerTransaction(final Merchant merchant, final BankAccount bankAccount,
                                 final double amount) {
        // First, collect the applicable discounts
        List<Cashback> applicableCashbacks = getPendingDiscounts(bankAccount).stream()
                .filter(cashback -> cashback.isApplicableFor(merchant))
                .toList();

        // Calculate total discount
        double totalDiscount = applicableCashbacks.stream()
                .map(Cashback::getPercentage)
                .reduce(0.0, Double::sum);

        // Then update the collections
        List<Cashback> accountAppliedDiscounts = getAppliedDiscounts(bankAccount);
        List<Cashback> accountPendingDiscounts = getPendingDiscounts(bankAccount);
        accountAppliedDiscounts.addAll(applicableCashbacks);
        accountPendingDiscounts.removeAll(applicableCashbacks);

        Optional<Cashback> discountOpt = merchant.registerTransaction(bankAccount, amount);

        // If the discount is not present, return the total discount
        if (discountOpt.isEmpty()) {
            return new PercentageDiscount(totalDiscount);
        }

        Cashback cashback = discountOpt.get();
        // If the discount is one time only, and it has already been applied, ignore it
        if (cashback.isApplicableOneTime()
                && accountAppliedDiscounts.stream().anyMatch(cashback::equals)) {
            return new PercentageDiscount(totalDiscount);
        }

        // If the discount is applicable now, apply it and return the new total discount
        if (cashback.isApplicableNow()) {
            accountAppliedDiscounts.add(cashback);
            return new PercentageDiscount(totalDiscount + cashback.getPercentage());
        } else if (!cashback.isApplicableOneTime()
                || accountPendingDiscounts.stream().noneMatch(cashback::equals)
                || accountAppliedDiscounts.stream().anyMatch(cashback::equals)) {
            // Otherwise, if the discount is not one time only, or it has not been applied yet,
            // or it is already pending, add it to the pending discounts
            accountPendingDiscounts.add(cashback);
        }

        return new PercentageDiscount(totalDiscount);
    }
}
