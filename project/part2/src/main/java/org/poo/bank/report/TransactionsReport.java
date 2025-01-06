package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import org.poo.bank.serialization.SerializationUtils;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Builder
@Getter
public final class TransactionsReport {
    @JsonProperty("IBAN")
    private final IBAN iban;
    @JsonSerialize(using = SerializationUtils.RoundedDoubleSerializer.class)
    private final double balance;
    private final Currency currency;
    private final List<TransactionLogView> transactions;
}
