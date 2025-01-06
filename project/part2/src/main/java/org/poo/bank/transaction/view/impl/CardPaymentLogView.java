package org.poo.bank.transaction.view.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.serialization.SerializationUtils;
import org.poo.bank.transaction.view.TransactionLogView;

@Getter
@SuperBuilder(toBuilder = true)
public final class CardPaymentLogView extends TransactionLogView {
    @JsonSerialize(using = SerializationUtils.RoundedDoubleSerializer.class)
    private final Double amount;
    @JsonProperty("commerciant")
    private final String merchant;
}
