package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.Hero;
import gwentstone.cards.Minion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlayerGameData {
    private final int deckIdx;
    private List<Minion> remCards;
    private List<Minion> hand = new ArrayList<Minion>();
    private Hero hero;

    PlayerGameData(int deckIdx, int shuffleSeed, Deck deck, Hero hero){
        this.deckIdx = deckIdx;
        remCards = deck.stream().map(Minion::copy).collect(Collectors.toList());
        Collections.shuffle(remCards, new Random(shuffleSeed));
        // This copy may not be needed
        this.hero = Hero.copy(hero);
    }
}
