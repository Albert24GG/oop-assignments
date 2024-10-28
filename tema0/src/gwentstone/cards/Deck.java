package gwentstone.cards;

import java.util.List;
import java.util.stream.Stream;

public class Deck {
    private List<Minion> minions;

    public Deck(List<Minion> minions) {
        this.minions = minions;
    }

    public Stream<Minion> stream() {
        return minions.stream();
    }
}
