package gwentstone.cards.impl;

import gwentstone.cards.PlayableCard;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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

    public void useAbility(@NonNull final List<@NonNull PlayableMinion> target) {
        config.getAbility().use(target);
    }

    private static final class Abilities{
        public static void subZero(@NonNull List<@NonNull PlayableMinion> targetRow){
           targetRow.forEach(m -> m.setFrozen(true));
        }

        public static void lowBlood(@NonNull List<@NonNull PlayableMinion> targetRow){
            Collections.max(targetRow, Comparator.comparingInt(PlayableCard::getCurrentHealth)).setCurrentHealth(0);
        }

        public static void earthBorn(@NonNull List<@NonNull PlayableMinion> targetRow){
            targetRow.forEach(m -> m.setCurrentHealth(m.getCurrentHealth() + 1));
        }

        public static void bloodThirst(@NonNull List<@NonNull PlayableMinion> targetRow){
            targetRow.forEach(m -> m.setCurrentAttackDamage(m.getCurrentAttackDamage() + 1));
        }
    }

    @RequiredArgsConstructor
    @Getter
    private enum Config{
        LORD_ROYCE(Abilities::subZero),
        EMPRESS_THORINA(Abilities::lowBlood),
        KING_MUDFACE(Abilities::earthBorn),
        GENERAL_KOCIORAW(Abilities::bloodThirst);

        @NonNull
        private final PlayableHeroAbility ability;

        public static Config getConfig(final Hero.Type type)
            throws IllegalArgumentException {
            return Config.valueOf(type.name());
        }
    }
}
