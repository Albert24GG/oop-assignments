package org.poo.bank.report.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;


@Getter
@SuperBuilder
public abstract class BusinessReport {
    @JsonProperty("IBAN")
    private final IBAN accountIban;
    private final double balance;
    private final Currency currency;
    @JsonProperty("spending limit")
    private final double spendingLimit;
    @JsonProperty("deposit limit")
    private final double depositLimit;
    @JsonProperty("statistics type")
    private final BusinessReportType type;
}
