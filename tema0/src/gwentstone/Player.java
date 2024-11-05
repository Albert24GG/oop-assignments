package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public final class Player {
    @Getter
    private int wins = 0;
    @Getter(AccessLevel.PACKAGE)
    private PlayerGameData gameData;
    private final List<Deck> decks;

    public Player(final List<Deck> decks) {
        this.decks = decks;
    }

    /**
     * Initialize the current game context
     *
     * @param deckIdx     The deck index that the player got
     * @param shuffleSeed The seed used for shuffling
     * @param hero        The hero that the player got
     */
    void initializeGameData(final int deckIdx, final int shuffleSeed, final Hero hero) {
        gameData = new PlayerGameData(deckIdx, shuffleSeed, decks.get(deckIdx), hero);
    }

    void addWin() {
        ++wins;
    }
}
