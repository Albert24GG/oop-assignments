package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.poo.bank.card.CardView;
import org.poo.bank.type.Currency;
import org.poo.bank.type.IBAN;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public final class BankAccView {
    @JsonProperty("IBAN")
    private final IBAN iban;
    private final double balance;
    private final Currency currency;
    private final BankAccountType type;
    private final List<CardView> cards;

    /**
     * Creates a new bank account view from a bank account
     *
     * @param account the bank account to create the view from
     * @return the new bank account view
     */
    public static BankAccView from(final BankAccount account) {
        return BankAccView.builder()
                .iban(account.getIban())
                .balance(new BigDecimal(Double.toString(account.getBalance())).setScale(2,
                        RoundingMode.HALF_UP).doubleValue())
                .currency(account.getCurrency())
                .type(account.getType())
                .cards(account.getCards().stream()
                        .map(CardView::from)
                        .toList())
                .build();
    }

}
