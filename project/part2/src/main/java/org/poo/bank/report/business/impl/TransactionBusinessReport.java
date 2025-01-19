package org.poo.bank.report.business.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.report.business.BusinessReport;

import java.util.List;

@SuperBuilder
@Getter
public final class TransactionBusinessReport extends BusinessReport {
    private final List<MemberReport> managers;
    private final List<MemberReport> employees;
    @JsonProperty("total spent")
    private final double totalSpent;
    @JsonProperty("total deposited")
    private final double totalDeposited;

    @Builder
    @Getter
    public static final class MemberReport {
        @JsonProperty("username")
        private final String name;
        private final double spent;
        private final double deposited;
    }
}
