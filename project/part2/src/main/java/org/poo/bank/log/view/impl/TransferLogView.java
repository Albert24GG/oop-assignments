package org.poo.bank.log.view.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.view.AuditLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class TransferLogView extends AuditLogView {
    private final IBAN senderIBAN;
    private final IBAN receiverIBAN;
    @JsonIgnore
    private final Double amount;
    @JsonIgnore
    private final Currency currency;
    private final String transferType;

    @JsonProperty("amount")
    public String getAmountAsString() {
        return amount + " " + currency;
    }
}
