package gwentstone.cards;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

public abstract class PlayableCard<T extends Card> {
    @NonNull
    @Getter(value = AccessLevel.PROTECTED)
    private final T card;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private int currentHealth;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private int currentAttackDamage;

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
        target.currentHealth = Math.max(0, target.currentHealth - this.card.getAttackDamage());
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
