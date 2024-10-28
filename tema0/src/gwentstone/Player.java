package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.Hero;
import gwentstone.cards.Minion;

import java.util.List;

public class Player {
    private int wins = 0;
    PlayerGameData gameData;
    public final List<Deck> decks;

    public Player(List<Deck> decks) {
       this.decks = decks;
    }

    public void initializeGameData(int deckIdx, int shuffleSeed, Hero hero){
        gameData = new PlayerGameData(deckIdx, shuffleSeed, decks.get(deckIdx), hero);
    }
}
