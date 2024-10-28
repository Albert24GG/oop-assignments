package gwentstone;

import gwentstone.cards.Hero;
import gwentstone.cards.Minion;

import java.util.List;

public class PlayerState {
    private int deckIdx;
    private List<Minion> remCards;
    private List<Minion> hand;
    private Hero hero;
}
