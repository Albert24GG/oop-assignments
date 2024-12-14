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
@Getter
public abstract class Card {
    private static final int LOW_BALANCE_THRESHOLD = 30;

    @EqualsAndHashCode.Exclude
    private final BankAccount linkedAccount;
    private final CardType type;
    @Setter(lombok.AccessLevel.PROTECTED)
    private CardNumber number = CardNumber.generate();
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, FROZEN, TO_BE_FROZEN, WARNING_LOW_BALANCE
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
     * Updates the status of the card
     * The status is updated based on the balance of the linked account
     *
     * @return the new status of the card
     */
    Status updateStatus() {
        status = switch (status) {
            case ACTIVE -> {
                double balanceDiff = linkedAccount.getBalance() - linkedAccount.getMinBalance();
                if (balanceDiff <= 0) {
                    yield Status.TO_BE_FROZEN;
                } else if (balanceDiff < LOW_BALANCE_THRESHOLD) {
                    yield Status.WARNING_LOW_BALANCE;
                } else {
                    yield status;
                }
            }
            case TO_BE_FROZEN -> Status.FROZEN;
            default -> status;
        };
        return status;
    }
}
