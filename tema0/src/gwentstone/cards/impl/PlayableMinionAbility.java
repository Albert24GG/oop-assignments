package gwentstone.cards.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class PlayableMinionAbility {
    private final AbilityTarget abilityTarget;
    /**
     * Use minion's ability on another minion
     *
     * @param attacker The attacker minion
     * @param target   The target minion
     */
    public abstract void use(PlayableMinion attacker, @NonNull PlayableMinion target);
}
