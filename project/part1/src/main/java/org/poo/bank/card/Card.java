package org.poo.bank.card;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.card.impl.DebitCard;
import org.poo.bank.card.impl.SingleUseCard;
import org.poo.bank.type.CardNumber;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public abstract class Card {
    private final BankAccount linkedAccount;
    private final CardType type;
    @Setter(lombok.AccessLevel.PROTECTED)
    private CardNumber number = CardNumber.generate();
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, FROZEN
    }

    static Card createCard(@NonNull final CardType type, BankAccount account) {
        Card card = switch (type) {
            case DEBIT -> new DebitCard(account);
            case SINGLE_USE -> new SingleUseCard(account);
            case null -> null;
        };
        if (card != null) {
            account.addCard(card);
        }
        return card;
    }

    /**
     * Gets the owner of the card
     *
     * @return the owner of the card
     */
    public UserAccount getOwner() {
        return linkedAccount.getOwner();
    }

    /**
     * Acknowledges that a payment has been made
     */
    protected abstract void paymentMade();
}
