package org.poo.bank.transaction.view.impl;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.poo.bank.serialization.SerializationUtils;
import org.poo.bank.transaction.view.TransactionLogView;
import org.poo.bank.type.IBAN;

@Getter
@SuperBuilder(toBuilder = true)
public final class SavingsWithdrawLogView extends TransactionLogView {
    @JsonSerialize(using = SerializationUtils.RoundedDoubleSerializer.class)
    private final Double amount;
    private final IBAN savingsAccountIBAN;
    private final IBAN classicAccountIBAN;
}
