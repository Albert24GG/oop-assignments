package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;
import gwentstone.cards.impl.PlayableHero;
import gwentstone.cards.impl.PlayableMinion;
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
     * Get player's remaining cards in deck in the current game.
     *
     * @return Deck made of the remaining cards that the player can draw
     */
    Deck getCurrentDeck() {
        // this should be called from GameManager, so gameData should be initialized
        return new Deck(
                gameData.getRemCards().stream().map(PlayableMinion::getUnderlyingCard).toList());
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
