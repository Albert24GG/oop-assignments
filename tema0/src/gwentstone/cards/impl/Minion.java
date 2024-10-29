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
    Minion(int mana, int health, int attackDamage, String description, List<String> colors, @NonNull String name) {
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

        public static Type fromString(String s)
                throws IllegalArgumentException {
            return valueOf(s.trim().replace(" ", "_").toUpperCase());
        }
    }
}
