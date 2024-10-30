package gwentstone.cards.impl;

import lombok.NonNull;

import java.util.List;

@FunctionalInterface
public interface PlayableHeroAbility {
    /**
     * Use hero's ability on a row of minions from the game board
     *
     * @param targetRow The row of minions
     */
    void use(@NonNull List<@NonNull PlayableMinion> targetRow);
}
