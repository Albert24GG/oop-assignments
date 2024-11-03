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
    @Getter(AccessLevel.PACKAGE)
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
     * Get player's remaining cards in deck in the current game.
     *
     * @return Deck made of the remaining cards that the player can draw
     */
    Deck getCurrentDeck() {
        return new Deck(remCards.stream().map(PlayableMinion::getUnderlyingCard).toList());
    }

    /**
     * Get the minion in hand at a certain index
     *
     * @param cardIdx index of the minion in hand
     * @return the minion
     */
    PlayableMinion getMinionInHand(final int cardIdx) {
        return hand.get(cardIdx);
    }

    /**
     * Remove a minion from the current hand
     *
     * @param cardIdx index of the card to remove
     */
    void removeMinionFromHand(final int cardIdx) {
        hand.remove(cardIdx);
    }

    private void addMana() {
        mana += manaIncrement;
        manaIncrement = Math.min(MAX_MANA_INCREMENT, manaIncrement + 1);
    }

    private void drawNextCard() {
        if (remCards.isEmpty()) {
            return;
        }
        hand.add(remCards.remove(0));
    }

    /**
     * Routine called at the start of a round.
     */
    void startRoundRoutine() {
        addMana();
        drawNextCard();
    }

    /**
     * Use player's mana.
     * This should be used when executing different actions that require mana.
     *
     * @param amount  The amount of mana to be used
     */
    void useMana(final int amount) {
        mana = Math.max(mana - amount, 0);
    }
}
