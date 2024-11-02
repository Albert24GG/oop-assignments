package gwentstone.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gwentstone.cards.Card;
import gwentstone.cards.Deck;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JsonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> CARD_BASE_FIELDS =
            Arrays.stream(Card.class.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toUnmodifiableSet());

    private JsonMapper() {
    }

    /**
     * Serialize a {@code Card} to a json {@code ObjectNode}
     *
     * @param card The {@code Card} object to serialize
     * @return The {@code ObjectNode} containing the serialized Card
     */
    public static ObjectNode mapCard(final Card card) {
        // we want to map only the fields from the Card class, even if its actual type is a derived
        return MAPPER.convertValue(card, ObjectNode.class).retain(CARD_BASE_FIELDS);
    }

    /**
     * Serialize a {@code Deck} to a json {@code ArrayNode}
     *
     * @param deck The {@code Deck} object to serialize
     * @return The {@code ArrayNode} containing the serialized cards from the deck
     */
    public static ArrayNode mapDeck(final Deck deck) {
        ArrayNode arrayNode = MAPPER.createArrayNode();
        deck.stream().forEach(card -> arrayNode.add(mapCard(card)));
        return arrayNode;
    }

    /**
     * Serialize a {@code List<Cards>} to a json {@code ArrayNode}
     *
     * @param cards The {@code List<Card>} object to serialize
     * @return The {@code ArrayNode} containing the serialized cards from the deck
     */
    public static ArrayNode mapCardList(final List<? extends Card> cards){
        ArrayNode arrayNode = MAPPER.createArrayNode();
        cards.forEach(card -> arrayNode.add(mapCard(card)));
        return arrayNode;
    }
}
