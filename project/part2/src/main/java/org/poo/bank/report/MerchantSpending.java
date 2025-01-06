package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.poo.bank.serialization.SerializationUtils;

public record MerchantSpending(
        @JsonProperty("commerciant")
        String merchant,

        @JsonSerialize(using = SerializationUtils.RoundedDoubleSerializer.class)
        @JsonProperty("total")
        double totalAmount
) {
}
