package org.poo.bank.card;

import lombok.Builder;
import lombok.Getter;
import org.poo.bank.type.CardNumber;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Getter
public final class CardView {
    private final CardNumber cardNumber;
    private final String status;

    /**
     * Creates a new card view from a card
     *
     * @param card the card to create the view from
     * @return the new card view
     */
    public static CardView from(final Card card) {
        return CardView.builder()
                .cardNumber(card.getNumber())
                .status(card.getStatus().toString().toLowerCase())
                .build();
    }
}
