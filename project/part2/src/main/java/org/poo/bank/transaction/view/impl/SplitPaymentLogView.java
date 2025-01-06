package org.poo.bank.transaction.view.impl;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.serialization.SerializationUtils;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public final class SplitPaymentLogView extends TransactionLogView {
    private final Currency currency;
    @JsonSerialize(using = SerializationUtils.RoundedDoubleSerializer.class)
    private final Double amount;
    private final List<IBAN> involvedAccounts;
}
