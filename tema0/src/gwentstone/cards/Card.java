package gwentstone.cards;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Card {
    private final int mana;
    private final int health;
    private final int attackDamage;
    private final String description;
    private final List<String> colors;
    private final String name;
}
