package gwentstone.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fileio.ActionsInput;
import gwentstone.GameManager;
import gwentstone.utils.JsonMapper;

import java.util.Map;
import java.util.function.Function;

public final class ActionFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<ActionsInput, Action>> ACTIONS = Map.of(
            "getPlayerTurn", GetPlayerTurn::new,
            "getPlayerDeck", GetPlayerDeck::new,
            "getPlayerHero", GetPlayerHero::new,
            "endPlayerTurn", EndPlayerTurn::new
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

    private static final class GetPlayerDeck extends Action {
        GetPlayerDeck(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonMapper.mapDeck(
                            gameManager.getPlayerDeck(getInput().getPlayerIdx())))
                    .build();
        }
    }

    private static final class GetPlayerHero extends Action {
        GetPlayerHero(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonMapper.mapCard(
                            gameManager.getPlayerHero(getInput().getPlayerIdx())))
                    .build();
        }
    }

    private static final class EndPlayerTurn extends Action {
        EndPlayerTurn(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.NONE)
                    .build();
        }
    }
}
