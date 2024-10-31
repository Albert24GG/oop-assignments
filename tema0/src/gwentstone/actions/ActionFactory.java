package gwentstone.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fileio.ActionsInput;
import gwentstone.GameManager;

import java.util.Map;
import java.util.function.Function;

public final class ActionFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<ActionsInput, Action>> ACTIONS = Map.of(
            "getPlayerTurn", GetPlayerTurn::new
    );

    private ActionFactory() {
    }

    /**
     * Create an Action for a given ActionsInput
     *
     * @param input The ActionsInput
     * @return The appropriate Action or null for invalid actions
     */
    public static Action getAction(final ActionsInput input) {
        var action = ACTIONS.get(input.getCommand());
        return action != null ? action.apply(input) : null;
    }


    private static final class GetPlayerTurn extends Action {
        GetPlayerTurn(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(final GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonNodeFactory.instance.numberNode(gameManager.getPlayerTurn()))
                    .build();
        }
    }
}
