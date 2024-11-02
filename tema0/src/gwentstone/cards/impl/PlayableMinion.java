package gwentstone.cards.impl;

import gwentstone.cards.PlayableCard;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public final class PlayableMinion extends PlayableCard<Minion> {
    @Getter
    private boolean isFrozen = false;

    @NonNull
    private final Config config;

    public PlayableMinion(@NonNull final Minion minion) {
        super(minion);
        config = Config.getConfig(minion.getType());
    }

    /**
     * Use the ability on a target minion
     *
     * @param target Target minion
     */
    public void useAbility(final PlayableMinion target) {
        if (isFrozen || this.config.getAbility() == null) {
            return;
        }

        this.config.getAbility().use(this, target);
    }

    /**
     * Freeze minion
     */
    public void freeze(){
        isFrozen = true;
    }

    /**
     * Unfreeze minion
     */
    public void unfreeze(){
        isFrozen = false;
    }

    @Override
    protected void setCurrentHealth(final int value) {
        super.setCurrentHealth(value);
    }

    @Override
    protected void setCurrentAttackDamage(final int value) {
        super.setCurrentAttackDamage(value);
    }

    public PlayableMinion.Placement getPlacement() {
        return this.config.getPlacement();
    }

    public boolean isTank() {
        return this.config.isTank();
    }

    public enum Placement {
        FRONT, BACK;
    }

    private static final class Abilities {
        public static void weakKnees(final PlayableMinion attacker,
                                     @NonNull final PlayableMinion target) {
            target.setCurrentAttackDamage(Math.max(0, target.getCurrentAttackDamage() - 2));
        }

        public static void skyjack(@NonNull final PlayableMinion attacker,
                                   @NonNull final PlayableMinion target) {
            int attackerHealth = attacker.getCurrentHealth();
            attacker.setCurrentHealth(target.getCurrentHealth());
            target.setCurrentHealth(attackerHealth);
        }

        public static void shapeshift(final PlayableMinion attacker,
                                      @NonNull final PlayableMinion target) {
            int targetHealth = target.getCurrentHealth();
            target.setCurrentHealth(target.getCurrentAttackDamage());
            target.setCurrentAttackDamage(targetHealth);
        }

        public static void godsPlan(final PlayableMinion attacker,
                                    @NonNull final PlayableMinion target) {
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

        public static Config getConfig(final Minion.Type type)
                throws IllegalArgumentException {
            return Config.valueOf(type.name());
        }
    }
}
