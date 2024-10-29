package gwentstone.cards;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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

    protected PlayableCard(@NonNull T card) {
        this.card = card;
        currentHealth = card.getHealth();
        currentAttackDamage = card.getAttackDamage();
    }

    public void attack(@NonNull PlayableCard<? extends Card> target){
        target.currentHealth = Math.max(0, target.currentHealth - this.card.getAttackDamage());
    }
}
