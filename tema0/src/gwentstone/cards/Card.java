package gwentstone.cards;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public abstract class Card {
    private final int mana;
    @Setter
    private int health;
    private final String description;
    private final List<String> colors;
    private final String name;


    protected Card(int mana, int health, String description, List<String> colors, String name) {
        this.description = description;
        this.colors = colors;
        this.name = name;
        this.health = health;
        this.mana = mana;
    }
}
