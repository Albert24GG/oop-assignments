package org.poo.gwentstone.cards;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class PlayableCard<T extends Card> {
    @NonNull
    private final T card;

    /**
     * Return the underlying card that represents this playable card
     *
     * @return The underlying card derived from {@link Card}
     */
    public final T getUnderlyingCard() {
        return card;
    }

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Integer currentHealth;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Integer currentAttackDamage;

    protected PlayableCard(@NonNull final T card) {
        this.card = card;
        currentHealth = card.getHealth();
        currentAttackDamage = card.getAttackDamage();
    }

    /**
     * Attack a target card by using brute force.
     * The damage taken by target is equal to the attackDamage of the attacker.
     * This can result in the target card having zero health left, which kills it.
     *
     * @param target Target card
     */
    public final void attack(@NonNull final PlayableCard<? extends Card> target) {
        if (currentAttackDamage == null) {
            throw new IllegalArgumentException("Attack damage cannot be null");
        }
        target.currentHealth = Math.max(0, target.currentHealth - currentAttackDamage);
    }

    public final int getMana() {
        return card.getMana();
    }

    /**
     * Get he base health of the card
     *
     * @return The base health of the card
     */
    public final int getHealth() {
        return card.getHealth();
    }
}
