package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.poo.bank.transaction.TransactionLog;
import org.poo.bank.transaction.TransactionLogView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.util.List;

@Builder
@Getter
public final class SpendingsReport {
    @JsonProperty("IBAN")
    private final IBAN iban;
    private final double balance;
    private final Currency currency;
    private final List<TransactionLogView> transactions;
    @JsonProperty("commerciants")
    private final List<MerchantSpending> merchants;
}
