package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;
import gwentstone.cards.impl.PlayableHero;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public class Player {
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
    public void initializeGameData(final int deckIdx, final int shuffleSeed, final Hero hero) {
        gameData = new PlayerGameData(deckIdx, shuffleSeed, decks.get(deckIdx), hero);
    }

    /**
     * Return player's deck in the current game
     *
     * @return An {@code Optional} containing the current deck, or an empty {@code Optional} if
     * there is no running game
     */
    Deck getCurrentDeck() {
        // this should be called from GameManager, so gameData should be initialized
        return decks.get(gameData.getDeckIdx());
    }

    /**
     * Get player's hero in the current game
     *
     * @return Player's hero
     */
    Hero getCurrentHero() {
        // this should be called from GameManager, so gameData should be initialized
        return gameData.getHero().getUnderlyingCard();
    }
}
