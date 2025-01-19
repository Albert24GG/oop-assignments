package org.poo.bank.report.business.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.report.business.BusinessReport;

import java.util.List;

@SuperBuilder
@Getter
public final class MerchantBusinessReport extends BusinessReport {
    @JsonProperty("commerciants")
    private final List<MerchantReport> merchants;

    @Builder
    @Getter
    public static class MerchantReport {
        @JsonProperty("commerciant")
        private final String merchantName;
        @JsonProperty("total received")
        private final double totalReceived;
        private final List<String> managers;
        private final List<String> employees;
    }
}
