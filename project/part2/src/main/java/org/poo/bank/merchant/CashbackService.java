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
        getPendingDiscounts(bankAccount).removeAll(applicableCashbacks);
        getAppliedDiscounts(bankAccount).addAll(applicableCashbacks);

        Optional<Cashback> discountOpt = merchant.registerTransaction(bankAccount, amount);

        // If the discount is not present, return the total discount
        if (discountOpt.isEmpty()) {
            return new PercentageDiscount(totalDiscount);
        }

        Cashback cashback = discountOpt.get();
        // If the discount is one time only, and it has already been applied, ignore it
        if (cashback.isApplicableOneTime()
                && getAppliedDiscounts(bankAccount).contains(cashback)) {
            return new PercentageDiscount(totalDiscount);
        }

        // If the discount is applicable now, apply it and return the new total discount
        if (cashback.isApplicableNow()) {
            getAppliedDiscounts(bankAccount).add(cashback);
            return new PercentageDiscount(totalDiscount + cashback.getPercentage());
        } else {
            // Otherwise, add it to the pending discounts and return the total discount
            getPendingDiscounts(bankAccount).add(cashback);
        }

        return new PercentageDiscount(totalDiscount);
    }
}
