package org.poo.gwentstone.cards.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.gwentstone.cards.PlayableCard;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PlayableHero extends PlayableCard<Hero> {
    @NonNull
    private final Config config;

    public PlayableHero(@NonNull final Hero hero) {
        super(hero);
        config = Config.getConfig(hero.getType());
    }

    /**
     * Use the ability on a row of minions
     *
     * @param target Target row of minions
     */
    public void useAbility(@NonNull final List<@NonNull PlayableMinion> target) {
        config.getAbility().use(target);
    }

    /**
     * Get the ability target of the hero.
     * The target can be either ENEMY (cards belonging to the enemy)
     * or PLAYER (cards belonging to the player).
     *
     * @return The ability target of the hero
     */
    public AbilityTarget getAbilityTarget() {
        return config.getAbility().getAbilityTarget();
    }

    private static final class Abilities {

        static final class SubZero extends PlayableHeroAbility {
            SubZero() {
                super(AbilityTarget.ENEMY);
            }

            @Override
            public void use(@NonNull final List<@NonNull PlayableMinion> targetRow) {
                targetRow.forEach(PlayableMinion::freeze);
            }
        }

        static final class LowBlood extends PlayableHeroAbility {
            LowBlood() {
                super(AbilityTarget.ENEMY);
            }

            @Override
            public void use(@NonNull final List<@NonNull PlayableMinion> targetRow) {
                Collections.max(targetRow, Comparator.comparingInt(PlayableCard::getCurrentHealth))
                        .setCurrentHealth(0);
            }
        }

        static final class EarthBorn extends PlayableHeroAbility {
            EarthBorn() {
                super(AbilityTarget.PLAYER);
            }

            @Override
            public void use(@NonNull final List<@NonNull PlayableMinion> targetRow) {
                targetRow.forEach(m -> m.setCurrentHealth(m.getCurrentHealth() + 1));
            }
        }

        static final class BloodThirst extends PlayableHeroAbility {
            BloodThirst() {
                super(AbilityTarget.PLAYER);
            }

            @Override
            public void use(@NonNull final List<@NonNull PlayableMinion> targetRow) {
                targetRow.forEach(m -> m.setCurrentAttackDamage(m.getCurrentAttackDamage() + 1));
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    private enum Config {
        LORD_ROYCE(new Abilities.SubZero()),
        EMPRESS_THORINA(new Abilities.LowBlood()),
        KING_MUDFACE(new Abilities.EarthBorn()),
        GENERAL_KOCIORAW(new Abilities.BloodThirst());

        @NonNull
        private final PlayableHeroAbility ability;

        public static Config getConfig(final Hero.Type type) {
            return Config.valueOf(type.name());
        }
    }
}
