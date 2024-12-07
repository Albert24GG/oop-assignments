package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public final class CardView {
    @JsonProperty("cardNumber")
    private final String number;
    private final String status;

    /**
     * Creates a new card view from a card
     *
     * @param card the card to create the view from
     * @return the new card view
     */
    public static CardView from(final Card card) {
        return CardView.builder()
                .number(card.getNumber())
                .status(card.isFrozen() ? "frozen" : "active")
                .build();
    }
}
