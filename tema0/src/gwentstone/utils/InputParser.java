package gwentstone.utils;

import fileio.CardInput;
import fileio.DecksInput;
import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;
import gwentstone.cards.impl.Minion;

import java.util.List;
import java.util.stream.Collectors;


public final class InputParser {
    private InputParser() {
    }

    /**
     * Parse a CardInput into a Minion
     *
     * @param input CardInput object to parse
     * @return The parsed Minion object
     */
    public static Minion parseMinion(final CardInput input) {
        return Minion
                .builder()
                .mana(input.getMana())
                .health(input.getHealth())
                .attackDamage(input.getAttackDamage())
                .description(input.getDescription())
                .colors(input.getColors())
                .name(input.getName())
                .build();
    }

    /**
     * Parse a list of CardInputs into a Deck
     *
     * @param input List of CardInputs to parse
     * @return The parsed Deck object
     */
    public static Deck parseDeck(final List<CardInput> input) {
        return new Deck(input.stream().map(InputParser::parseMinion).collect(Collectors.toList()));
    }

    /**
     * Parse a DecksInput into a list of Decks
     *
     * @param input DecksInput object to parse
     * @return The parsed List of Decks
     */
    public static List<Deck> parseDeckList(final DecksInput input) {
        return input.getDecks().stream().map(InputParser::parseDeck).collect(Collectors.toList());
    }

    /**
     * Parse a CardInput into a Hero
     *
     * @param input CardInput object to parse
     * @return The parsed Hero object
     */
    public static Hero parseHero(final CardInput input) {
        return Hero
                .builder()
                .mana(input.getMana())
                .description(input.getDescription())
                .colors(input.getColors())
                .name(input.getName())
                .build();
    }


}
