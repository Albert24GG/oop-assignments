package gwentstone.cards.impl;

import gwentstone.cards.Card;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
public final class Hero extends Card {
    private static final int HEALTH = 30;

    private final Type type;

    @Builder
    Hero(int mana, int attackDamage, String description, List<String> colors, @NonNull String name) {
        super(mana, HEALTH, attackDamage, description, colors, name);
        this.type = Type.fromString(name);
    }

    public enum Type {
        LORD_ROYCE,
        EMPRESS_THORINA,
        KING_MUDFACE,
        GENERAL_KOCIORAW;

        public static Type fromString(String s)
                throws IllegalArgumentException {
            return valueOf(s.trim().replace(" ", "_").toUpperCase());
        }
    }
}
