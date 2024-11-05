package gwentstone.cards.impl;

import gwentstone.cards.Card;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter()
public final class Minion extends Card {
    private final Type type;

    @Builder
    Minion(final Integer mana, final Integer health, final Integer attackDamage,
           final String description,
           final List<String> colors,
           @NonNull final String name) {
        super(mana, health, attackDamage, description, colors, name);
        this.type = Type.fromString(name);
    }

    public enum Type {
        SENTINEL,
        BERSERKER,
        GOLIATH,
        WARDEN,
        THE_RIPPER,
        MIRAJ,
        THE_CURSED_ONE,
        DISCIPLE;


        /**
         * Convert a string to the appropriate minion type
         *
         * @param s String to convert
         * @return The corresponding minion type
         */
        public static Type fromString(final String s) {
            return valueOf(s.trim().replace(" ", "_").toUpperCase());
        }
    }
}
