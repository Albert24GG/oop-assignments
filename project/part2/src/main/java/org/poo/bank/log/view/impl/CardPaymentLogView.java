package org.poo.bank.log.view.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.log.view.AuditLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLogView extends AuditLogView {
    private final Double amount;
    @JsonProperty("commerciant")
    private final String merchant;
}
