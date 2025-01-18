package org.poo.bank.eventSystem.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.splitPayment.SplitPayment;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.SplitPaymentLog;

@Getter
@RequiredArgsConstructor
public final class SplitPaymentEvent {
    private final SplitPayment payment;
    private final Type type;

    public enum Type {
        REJECTED,
        ACCEPTED
    }

    /**
     * Create a new accepted event for the given payment.
     *
     * @param payment the accepted split payment
     * @return the event for the accepted payment
     */
    public static SplitPaymentEvent accept(final SplitPayment payment) {
        return new SplitPaymentEvent(payment, Type.ACCEPTED);
    }

    /**
     * Create a new rejected event for the given payment.
     *
     * @param payment the rejected split payment
     * @return the event for the rejected payment
     */
    public static SplitPaymentEvent reject(final SplitPayment payment) {
        return new SplitPaymentEvent(payment, Type.REJECTED);
    }

    /**
     * Get the transaction log for the event.
     *
     * @return the transaction log for the event
     */
    public SplitPaymentLog getTransactionLog() {
        double totalAmount = payment.getAmountPerAccount().stream().reduce(0.0, Double::sum);
        var transactionLog = SplitPaymentLog.builder()
                .timestamp(payment.getTimestamp())
                .logType(AuditLogType.SPLIT_PAYMENT)
                .logStatus(type == Type.ACCEPTED ? AuditLogStatus.SUCCESS : AuditLogStatus.FAILURE)
                .description(String.format("Split payment of %.2f %s", totalAmount,
                        payment.getCurrency()))
                .type(payment.getType())
                .currency(payment.getCurrency())
                .involvedAccounts(
                        payment.getInvolvedAccounts().stream().map(BankAccount::getIban).toList());

        switch (payment.getType()) {
            case EQUAL -> transactionLog.amount(totalAmount / payment.getInvolvedAccounts().size());
            case CUSTOM -> transactionLog.amountPerAccount(payment.getAmountPerAccount());
            default -> {
            }
        }

        if (type == Type.REJECTED) {
            transactionLog.error("One user rejected the payment.");
        }

        return transactionLog.build();
    }
}
