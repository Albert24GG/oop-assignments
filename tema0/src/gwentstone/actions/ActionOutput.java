package gwentstone.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import lombok.Builder;
import lombok.NonNull;

import java.util.Optional;

@Builder
public final class ActionOutput<T extends BaseJsonNode> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Builder.Default
    private final Type type = Type.NONE;
    private final T actionOutput;
    @NonNull
    private final ActionsInput actionInput;

    protected enum Type {
        OUTPUT, ERROR, NONE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Map the action output to a Json node
     *
     * @return An {@code Optional} containing the JsonNode, or an empty {@code Optional} in case of
     * output
     */
    public Optional<JsonNode> toJson() {
        if (type == Type.NONE || actionOutput == null) {
            return Optional.empty();
        }
        return Optional.of(MAPPER.convertValue(actionInput, ObjectNode.class)
                .set(type.toString(), actionOutput));
    }
}
