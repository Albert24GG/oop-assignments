package org.poo.bank.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;
import org.poo.bank.servicePlan.ServicePlanType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Getter
public final class TransactionLogView {
    @NonNull
    private final Integer timestamp;
    private final String description;
    private final String error;
    private final CardNumber card;
    private final Email cardHolder;
    private final IBAN account;
    private final IBAN accountIBAN;
    @JsonProperty("commerciant")
    private final String merchant;
    private final Currency currency;
    private final List<IBAN> involvedAccounts;
    private final IBAN senderIBAN;
    private final IBAN receiverIBAN;
    @JsonIgnore
    private final Double amountAsDouble;
    @JsonIgnore
    private final String amountAsString;
    private final String transferType;
    @JsonIgnore
    private final TransactionLogType type;
    private final IBAN savingsAccountIBAN;
    private final IBAN classicAccountIBAN;
    private final ServicePlanType newPlanType;

    @JsonProperty("amount")
    public Object getAmount() {
        if (amountAsDouble != null) {
            return new BigDecimal(Double.toString(amountAsDouble)).setScale(2,
                    RoundingMode.HALF_UP).doubleValue();
        } else {
            return amountAsString;
        }
    }
}

