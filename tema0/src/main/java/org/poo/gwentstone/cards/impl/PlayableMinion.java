package org.poo.gwentstone.cards.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.gwentstone.cards.PlayableCard;

import java.util.Optional;

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
        if (isFrozen) {
            return;
        }

        PlayableMinionAbility ability = this.config.getAbility();
        if (ability == null) {
            throw new IllegalStateException("Minion has no ability");
        }

        this.config.getAbility().use(this, target);
    }

    /**
     * Freeze minion
     */
    public void freeze() {
        isFrozen = true;
    }

    /**
     * Unfreeze minion
     */
    public void unfreeze() {
        isFrozen = false;
    }

    /**
     * Get the ability target of the minion.
     * The target can be either ENEMY (cards belonging to the enemy)
     * or PLAYER (cards belonging to the player).
     *
     * @return An {@code Optional} containing the ability target, or an empty {@code Optional} if
     * the minion has no ability
     */
    public Optional<AbilityTarget> getAbilityTarget() {
        if (config.getAbility() == null) {
            return Optional.empty();
        }
        return Optional.of(config.getAbility().getAbilityTarget());
    }

    @Override
    protected void setCurrentHealth(final Integer value) {
        super.setCurrentHealth(value);
    }

    @Override
    protected void setCurrentAttackDamage(final Integer value) {
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

        static final class WeakKnees extends PlayableMinionAbility {
            WeakKnees() {
                super(AbilityTarget.ENEMY);
            }

            @Override
            public void use(final PlayableMinion attacker,
                            @NonNull final PlayableMinion target) {
                target.setCurrentAttackDamage(Math.max(0, target.getCurrentAttackDamage() - 2));
            }
        }

        static final class Skyjack extends PlayableMinionAbility {
            Skyjack() {
                super(AbilityTarget.ENEMY);
            }

            @Override
            public void use(@NonNull final PlayableMinion attacker,
                            @NonNull final PlayableMinion target) {
                int attackerHealth = attacker.getCurrentHealth();
                attacker.setCurrentHealth(target.getCurrentHealth());
                target.setCurrentHealth(attackerHealth);
            }

        }

        static final class ShapeShift extends PlayableMinionAbility {
            ShapeShift() {
                super(AbilityTarget.ENEMY);
            }

            @Override
            public void use(final PlayableMinion attacker,
                            @NonNull final PlayableMinion target) {
                int targetHealth = target.getCurrentHealth();
                target.setCurrentHealth(target.getCurrentAttackDamage());
                target.setCurrentAttackDamage(targetHealth);
            }
        }

        static final class GodsPlan extends PlayableMinionAbility {
            GodsPlan() {
                super(AbilityTarget.PLAYER);
            }

            @Override
            public void use(final PlayableMinion attacker,
                            @NonNull final PlayableMinion target) {
                target.setCurrentHealth(target.getCurrentHealth() + 2);
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum Config {
        SENTINEL(false, Placement.BACK, null),
        BERSERKER(false, Placement.BACK, null),
        GOLIATH(true, Placement.FRONT, null),
        WARDEN(true, Placement.FRONT, null),
        THE_RIPPER(false, Placement.FRONT, new Abilities.WeakKnees()),
        MIRAJ(false, Placement.FRONT, new Abilities.Skyjack()),
        THE_CURSED_ONE(false, Placement.BACK, new Abilities.ShapeShift()),
        DISCIPLE(false, Placement.BACK, new Abilities.GodsPlan());

        private final boolean isTank;
        private final Placement placement;
        private final PlayableMinionAbility ability;

        public static Config getConfig(final Minion.Type type) {
            return Config.valueOf(type.name());
        }
    }
}
