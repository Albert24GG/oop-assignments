package org.poo.bank.payment.request;

import org.poo.bank.payment.request.impl.CardPaymentRequest;
import org.poo.bank.payment.request.impl.SplitPaymentRequest;
import org.poo.bank.payment.request.impl.TransferRequest;

import java.util.Set;

/**
 * Registry of allowed payment requests
 * This registry is used to ensure that only allowed payment requests are used
 */
final class PaymentRequestRegistry {
    private static final Set<Class<? extends PaymentRequest>> ALLOWED_PAYMENT_REQUESTS = Set.of(
            CardPaymentRequest.class,
            SplitPaymentRequest.class,
            TransferRequest.class
    );

    private PaymentRequestRegistry() {
    }

    static void validatePaymentRequest(final PaymentRequest paymentRequest) {
        if (!ALLOWED_PAYMENT_REQUESTS.contains(paymentRequest.getClass())) {
            throw new SecurityException("Unauthorized payment request");
        }
    }
}
