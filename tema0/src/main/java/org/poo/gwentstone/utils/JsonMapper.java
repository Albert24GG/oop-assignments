package org.poo.gwentstone.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.gwentstone.cards.Card;
import org.poo.gwentstone.cards.PlayableCard;

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
     * Serialize a {@code PlayableCard} to a json {@code ObjectNode}
     *
     * @param card The {@code PlayableCard} object to serialize
     * @return The {@code ObjectNode} containing the serialized PlayableCard
     */
    public static ObjectNode mapPlayableCard(final PlayableCard<? extends Card> card) {
        ObjectNode jsonCard = mapCard(card.getUnderlyingCard());

        jsonCard.replace("health", JsonNodeFactory.instance.numberNode(card.getCurrentHealth()));

        if (card.getCurrentAttackDamage() != null) {
            jsonCard.replace("attackDamage",
                    JsonNodeFactory.instance.numberNode(card.getCurrentAttackDamage()));
        }

        return jsonCard;
    }
}
