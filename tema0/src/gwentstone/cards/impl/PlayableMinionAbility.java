package gwentstone.cards.impl;

import lombok.NonNull;

@FunctionalInterface
public interface PlayableMinionAbility{
   void use(@NonNull PlayableMinion attacker, @NonNull PlayableMinion target);
}
