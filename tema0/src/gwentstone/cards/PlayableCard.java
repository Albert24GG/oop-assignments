package gwentstone.cards;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

public abstract class PlayableCard<T extends Card> {
    @NonNull
    private final T card;

    /**
     * Return the underlying card that represents this playable card
     *
     * @return The underlying card
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
        if (this.currentAttackDamage == null) {
            throw new IllegalArgumentException("Attack damage cannot be null");
        }
        target.currentHealth = Math.max(0, target.currentHealth - this.currentAttackDamage);
    }

    public final int getMana() {
        return this.card.getMana();
    }

    public final int getHealth() {
        return this.card.getHealth();
    }

    public final int getAttackDamage() {
        return this.card.getAttackDamage();
    }

    public final String getDescription() {
        return this.card.getDescription();
    }

    public final List<String> getColors() {
        return this.card.getColors();
    }

    public final String getName() {
        return this.card.getName();
    }
}
