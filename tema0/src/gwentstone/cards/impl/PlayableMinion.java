package gwentstone.cards.impl;

import gwentstone.cards.PlayableCard;
import lombok.*;

public final class PlayableMinion extends PlayableCard<Minion> {
    @Setter(value = AccessLevel.PACKAGE)
    private boolean isFrozen = false;

    @NonNull
    private final Config config;

    public PlayableMinion(@NonNull Minion minion) {
        super(minion);
        config = Config.getConfig(minion.getType());
    }

    public void useAbility(PlayableMinion target){
        if(isFrozen || this.config.getAbility() == null){
            return;
        }

       this.config.getAbility().use(this, target);
    }

    @Override
    protected void setCurrentHealth(int value){
       super.setCurrentHealth(value);
    }

    @Override
    protected void setCurrentAttackDamage(int value){
        super.setCurrentAttackDamage(value);
    }

    public enum Placement {
        FRONT, BACK;
    }

    private static final class Abilities {
        public static void weakKnees(PlayableMinion attacker, @NonNull PlayableMinion target) {
            target.setCurrentAttackDamage(Math.max(0, target.getCurrentAttackDamage() - 2));
        }

        public static void skyjack(@NonNull PlayableMinion attacker, @NonNull PlayableMinion target) {
            int attackerHealth = attacker.getCurrentHealth();
            attacker.setCurrentHealth(target.getCurrentHealth());
            target.setCurrentHealth(attackerHealth);
        }

        public static void shapeshift(PlayableMinion attacker, @NonNull PlayableMinion target) {
            int targetHealth = target.getCurrentHealth();
            target.setCurrentHealth(target.getCurrentAttackDamage());
            target.setCurrentAttackDamage(targetHealth);
        }

        public static void godsPlan(PlayableMinion attacker, @NonNull PlayableMinion target) {
            target.setCurrentHealth(target.getCurrentHealth() + 2);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum Config {
        SENTINEL(false, Placement.BACK, null),
        BERSERKER(false, Placement.BACK, null),
        GOLIATH(true, Placement.FRONT, null),
        WARDEN(true, Placement.FRONT, null),
        THE_RIPPER(false, Placement.FRONT, Abilities::weakKnees),
        MIRAJ(false, Placement.FRONT, Abilities::skyjack),
        THE_CURSED_ONE(false, Placement.BACK, Abilities::shapeshift),
        DISCIPLE(false, Placement.BACK, Abilities::godsPlan);

        private final boolean isTank;
        private final Placement placement;
        private final PlayableMinionAbility ability;

        public static Config getConfig(Minion.Type type)
                throws IllegalArgumentException {
            return Config.valueOf(type.name());
        }
    }
}
