package org.poo.gwentstone.cards.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class PlayableHeroAbility {
    private final AbilityTarget abilityTarget;

    /**
     * Use hero's ability on a row of minions from the game board
     *
     * @param targetRow The row of minions
     */
    public abstract void use(@NonNull List<@NonNull PlayableMinion> targetRow);
}
