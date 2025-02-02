package org.poo.gwentstone;

import lombok.Getter;
import org.poo.gwentstone.cards.Deck;
import org.poo.gwentstone.cards.impl.Hero;
import org.poo.gwentstone.cards.impl.PlayableHero;
import org.poo.gwentstone.cards.impl.PlayableMinion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

final class PlayerGameData {
    @Getter
    private final int deckIdx;
    @Getter
    private List<PlayableMinion> remCards;
    @Getter
    private List<PlayableMinion> hand = new ArrayList<>();
    @Getter
    private final PlayableHero hero;
    /**
     * Variable indicating if the player used hero's ability in the current round
     */
    @Getter
    private boolean usedHeroAbility = false;
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
     * Mark the hero ability as used in the current round
     */
    void markUsedHeroAbility() {
        usedHeroAbility = true;
    }

    /**
     * Reset the history of used hero ability
     */
    void resetUsedHeroAbility() {
        usedHeroAbility = false;
    }

    /**
     * Get player's remaining cards in deck in the current game.
     *
     * @return Deck made of the remaining cards that the player can draw
     */
    Deck getCurrentDeck() {
        return new Deck(remCards.stream().map(PlayableMinion::getUnderlyingCard).toList());
    }

    PlayableMinion getMinionInHand(final int cardIdx) {
        return hand.get(cardIdx);
    }

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
        hand.add(remCards.removeFirst());
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
     * @param amount The amount of mana to be used
     */
    void useMana(final int amount) {
        mana = Math.max(mana - amount, 0);
    }
}
