package gwentstone.cards;

import gwentstone.cards.impl.Minion;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class Deck {
    private final List<Minion> minions;

    public Stream<Minion> stream() {
        return minions.stream();
    }
}
