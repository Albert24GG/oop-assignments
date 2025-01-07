package org.poo.bank.splitPayment;

import lombok.RequiredArgsConstructor;
import org.poo.bank.account.UserAccount;
import org.poo.bank.eventSystem.BankEventService;
import org.poo.bank.eventSystem.events.SplitPaymentEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class SplitPaymentService {
    private final Map<UserAccount, List<SplitPayment>> accountPayments = new HashMap<>();
    private final BankEventService eventService;

    private enum PaymentStatus {
        ACCEPTED,
        REJECTED
    }

    /**
     * Register a payment to be processed.
     *
     * @param payment the payment to register
     */
    public void registerPayment(final SplitPayment payment) {
        payment.getAccountsInvolved().forEach(account -> {
            accountPayments.computeIfAbsent(account.getOwner(), k -> new ArrayList<>())
                    .add(payment);
        });
    }

    private void finalizePayment(final SplitPayment payment, final PaymentStatus status) {
        switch (status) {
            case ACCEPTED -> eventService.post(SplitPaymentEvent.accept(payment));
            case REJECTED -> eventService.post(SplitPaymentEvent.reject(payment));
            default -> {
            }
        }

        payment.getAccountsInvolved().forEach(account -> {
            Optional.ofNullable(accountPayments.get(account.getOwner()))
                    .ifPresent(payments -> payments.remove(payment));
        });
    }

    /**
     * Confirm a payment for the given account.
     *
     * @param userAccount the account to confirm the payment for. The first payment of the user
     *                    will be confirmed.
     */
    public void confirmPayment(final UserAccount userAccount) {
        accountPayments.getOrDefault(userAccount, List.of()).stream().findFirst()
                .ifPresent(payment -> {
                    payment.confirmPayment(userAccount);
                    accountPayments.get(userAccount).remove(payment);
                    if (payment.isPaymentConfirmed()) {
                        finalizePayment(payment, PaymentStatus.ACCEPTED);
                    }
                });
    }

    /**
     * Reject a payment for the given account.
     *
     * @param userAccount the account to reject the payment for. The first payment of the user
     *                    will be rejected.
     */
    public void rejectPayment(final UserAccount userAccount) {
        accountPayments.getOrDefault(userAccount, List.of()).stream().findFirst()
                .ifPresent(payment -> {
                    finalizePayment(payment, PaymentStatus.REJECTED);
                });
    }

}
