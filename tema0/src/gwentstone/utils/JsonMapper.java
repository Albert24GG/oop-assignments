package gwentstone.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gwentstone.cards.Card;
import gwentstone.cards.Deck;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class JsonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> CARD_BASE_FIELDS =
            Arrays.stream(Card.class.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toUnmodifiableSet());

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonMapper() {
    }

    public static ObjectNode mapCard(final Card card) {
        // we want to map only the fields from the Card class, even if its actual type is a derived
        return MAPPER.convertValue(card, ObjectNode.class).retain(CARD_BASE_FIELDS);
    }

    public static ArrayNode mapDeck(final Deck deck) {
        ArrayNode arrayNode = MAPPER.createArrayNode();
        deck.stream().forEach(card -> arrayNode.add(mapCard(card)));
        return arrayNode;
    }
}
