package gwentstone.cards;

import java.util.List;

public abstract class Card {
    private final int mana;
    private int health;
    private final String description;
    private final List<String> colors;
    private final String name;

    public int getMana() {
        return mana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getColors() {
        return colors;
    }

    public String getName() {
        return name;
    }

    protected Card(int mana, int health, String description, List<String> colors, String name) {
        this.description = description;
        this.colors = colors;
        this.name = name;
        this.health = health;
        this.mana = mana;
    }
}
