package gwentstone.cards.impl;

import lombok.NonNull;

import java.util.List;

@FunctionalInterface
public interface PlayableHeroAbility {
    void use(@NonNull List<@NonNull PlayableMinion> targetRow);
}
