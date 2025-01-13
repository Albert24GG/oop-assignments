package org.poo.bank.report.business.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class BusinessMemberReport {
    @JsonProperty("username")
    private final String name;
    private final double spent;
    private final double deposited;
}
