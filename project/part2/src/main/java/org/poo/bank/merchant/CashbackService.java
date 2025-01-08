package org.poo.bank.merchant;

import org.poo.bank.account.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class CashbackService {
    private final Map<BankAccount, List<Discount>> pendingDiscounts = new HashMap<>();
    private final Map<BankAccount, List<Discount>> appliedDiscounts = new HashMap<>();

    private List<Discount> getPendingDiscounts(final BankAccount bankAccount) {
        return pendingDiscounts.computeIfAbsent(bankAccount, k -> new ArrayList<>());
    }

    private List<Discount> getAppliedDiscounts(final BankAccount bankAccount) {
        return appliedDiscounts.computeIfAbsent(bankAccount, k -> new ArrayList<>());
    }

    double registerTransaction(final Merchant merchant, final BankAccount bankAccount,
                               final double amount) {
        // First, collect the applicable discounts
        List<Discount> applicableDiscounts = getPendingDiscounts(bankAccount).stream()
                .filter(discount -> discount.isApplicableFor(merchant))
                .toList();

        // Calculate total discount
        double totalDiscount = applicableDiscounts.stream()
                .map(Discount::getPercentage)
                .reduce(0.0, Double::sum);

        // Then update the collections
        getPendingDiscounts(bankAccount).removeAll(applicableDiscounts);
        getAppliedDiscounts(bankAccount).addAll(applicableDiscounts);

        Optional<Discount> discountOpt = merchant.registerTransaction(bankAccount, amount);

        // If the discount is not present, return the total discount
        if (discountOpt.isEmpty()) {
            return totalDiscount;
        }

        Discount discount = discountOpt.get();
        // If the discount is one time only, and it has already been applied, ignore it
        if (discount.isApplicableOneTime()
                && getAppliedDiscounts(bankAccount).contains(discount)) {
            return totalDiscount;
        }

        // If the discount is applicable now, apply it and return the new total discount
        if (discount.isApplicableNow()) {
            getAppliedDiscounts(bankAccount).add(discount);
            return totalDiscount + discount.getPercentage();
        } else {
            // Otherwise, add it to the pending discounts and return the total discount
            getPendingDiscounts(bankAccount).add(discount);
        }

        return totalDiscount;
    }
}
