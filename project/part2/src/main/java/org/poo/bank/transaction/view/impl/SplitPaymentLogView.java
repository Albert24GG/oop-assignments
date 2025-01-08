package org.poo.bank.transaction.view.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.splitPayment.SplitPaymentType;
import org.poo.bank.transaction.view.AuditLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public final class SplitPaymentLogView extends AuditLogView {
    private final Currency currency;
    private final Double amount;
    private final List<IBAN> involvedAccounts;
    @JsonProperty("amountForUsers")
    private final List<Double> amountPerAccount;
    private final SplitPaymentType splitPaymentType;
}
