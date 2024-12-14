package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MerchantSpending(@JsonProperty("commerciant") String merchant,
                               @JsonProperty("total") double totalAmount) {
}
