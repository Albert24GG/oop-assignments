package org.poo.bank.payment.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.poo.bank.payment.PaymentContext;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class PaymentRequest {
    @NonNull
    private final Integer timestamp;
    @NonNull
    private final Double amount;

    /**
     * Process the payment request
     *
     * @param context the payment context
     */
    public final void process(final PaymentContext context) {
        PaymentRequestRegistry.validatePaymentRequest(this);

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        internalProcess(context);
    }

    protected abstract void internalProcess(final PaymentContext context);
}
