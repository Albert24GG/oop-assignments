package org.poo.bank.transaction.view.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.transaction.view.TransactionLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLogView extends TransactionLogView {
    private final Double amount;
    @JsonProperty("commerciant")
    private final String merchant;
}
