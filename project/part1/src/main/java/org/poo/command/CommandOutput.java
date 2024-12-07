package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class CommandOutput {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String command;
    private final int timestamp;
    private final JsonNode output;

    public JsonNode toJson() {
        return MAPPER.valueToTree(this);
    }
}
