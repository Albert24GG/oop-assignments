package org.poo.gwentstone.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import org.poo.fileio.ActionsInput;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public final class ActionOutput {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Builder.Default
    private final Type type = Type.NONE;
    private final BaseJsonNode actionOutput;
    @Builder.Default
    private final ActionsInput actionInput = null;

    protected enum Type {
        OUTPUT, ERROR, GAME_ENDED, NONE;

        @Override
        public String toString() {
            return Arrays.stream(name().toLowerCase().split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.joining())
                    .replaceFirst("^.",
                            Character.toString(Character.toLowerCase(name().charAt(0))));
        }
    }

    /**
     * Map the action output to a Json node
     *
     * @return An {@code Optional} containing the JsonNode, or an empty {@code Optional} in case of
     * output
     */
    public Optional<JsonNode> toJson() {
        if (type == Type.NONE) {
            return Optional.empty();
        }

        ObjectNode outputNode =
                actionInput == null ? MAPPER.createObjectNode() : MAPPER.valueToTree(actionInput);

        return Optional.of(outputNode.set(type.toString(), actionOutput));

    }
}
