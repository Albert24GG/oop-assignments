package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.poo.bank.card.CardView;

import java.util.List;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public final class BankAccView {
    @JsonProperty("IBAN")
    private final String iban;
    private final double balance;
    private final String currency;
    private final String type;
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
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .type(account.getType().toString().toLowerCase())
                .cards(account.getCards().stream()
                        .map(CardView::from)
                        .toList())
                .build();
    }

}
