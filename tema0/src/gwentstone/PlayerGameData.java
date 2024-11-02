package gwentstone;

import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;
import gwentstone.cards.impl.PlayableHero;
import gwentstone.cards.impl.PlayableMinion;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class PlayerGameData {
    @Getter
    private final int deckIdx;
    @Getter(AccessLevel.PACKAGE)
    private List<PlayableMinion> remCards;
    private List<PlayableMinion> hand = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final PlayableHero hero;
    @Getter
    private int mana = 0;
    private int manaIncrement = 1;
    private static final int MAX_MANA_INCREMENT = 10;

    PlayerGameData(final int deckIdx, final int shuffleSeed, final Deck deck, final Hero hero) {
        this.deckIdx = deckIdx;
        remCards = deck.stream().map(PlayableMinion::new).collect(Collectors.toList());
        Collections.shuffle(remCards, new Random(shuffleSeed));
        this.hero = new PlayableHero(hero);
    }

    /**
     * Get the minion in hand at a certain index
     *
     * @param cardIdx index of the minion in hand
     * @return the minion
     */
    public PlayableMinion getMinionInHand(final int cardIdx) {
        return remCards.get(cardIdx);
    }

    /**
     * Remove a minion from the current hand
     *
     * @param cardIdx index of the card to remove
     */
    public void removeMinionFromHand(final int cardIdx) {
        hand.remove(cardIdx);
    }

    void addMana() {
        mana += manaIncrement;
        manaIncrement = Math.min(MAX_MANA_INCREMENT, manaIncrement + 1);
    }

    void drawNextCard() {
        if (remCards.isEmpty()) {
            return;
        }
        hand.add(remCards.remove(0));
    }
}
