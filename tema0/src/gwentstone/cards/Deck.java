package gwentstone.cards;

import gwentstone.cards.impl.Minion;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class Deck {
    private final List<Minion> minions;

    /**
     * Returns a stream from the underlying deck consisting of a list of minions
     *
     * @return the stream
     */
    public Stream<Minion> stream() {
        return minions.stream();
    }
}
