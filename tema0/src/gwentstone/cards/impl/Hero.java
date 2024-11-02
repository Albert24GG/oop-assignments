package gwentstone.cards.impl;

import gwentstone.cards.Card;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public final class Hero extends Card {
    private static final int HEALTH = 30;

    private final Type type;

    @Builder
    Hero(final Integer mana, final Integer attackDamage, final String description,
         final List<String> colors, @NonNull final String name) {
        super(mana, HEALTH, attackDamage, description, colors, name);
        this.type = Type.fromString(name);
    }

    public enum Type {
        LORD_ROYCE,
        EMPRESS_THORINA,
        KING_MUDFACE,
        GENERAL_KOCIORAW;

        /**
         * Convert a string to the appropriate hero type
         *
         * @param s String to convert
         * @return The corresponding hero type
         * @throws IllegalArgumentException If a string cannot be converted to a hero type
         */
        public static Type fromString(final String s)
                throws IllegalArgumentException {
            return valueOf(s.trim().replace(" ", "_").toUpperCase());
        }
    }
}
