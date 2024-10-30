package gwentstone.cards.impl;

import lombok.NonNull;

@FunctionalInterface
public interface PlayableMinionAbility {
    /**
     * Use minion's ability on another minion
     *
     * @param attacker The attacker minion
     * @param target   The target minion
     */
    void use(@NonNull PlayableMinion attacker, @NonNull PlayableMinion target);
}
