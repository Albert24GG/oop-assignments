package gwentstone.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
            "endPlayerTurn", EndPlayerTurn::new,
            "getCardsInHand", GetCardsInHand::new,
            "getCardsOnTable", GetCardsOnTable::new,
            "getPlayerMana", GetPlayerMana::new,
            "placeCard", PlaceCard::new
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

    private static final class PlaceCard extends Action {
        PlaceCard(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(final GameManager gameManager) {
            var actionOutput = ActionOutput.builder();
            try {
                gameManager.placeCard(getInput().getHandIdx());
                return actionOutput.type(ActionOutput.Type.NONE).build();
            } catch (final ActionException err) {
                return actionOutput
                        .type(ActionOutput.Type.ERROR)
                        .actionInput(getInput())
                        .actionOutput(JsonNodeFactory.instance.textNode(err.getMessage()))
                        .build();
            }
        }
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
            ArrayNode actionOutput = MAPPER.valueToTree(
                    gameManager.getPlayerDeck(getInput().getPlayerIdx())
                            .stream()
                            .map(JsonMapper::mapCard)
                            .toList()
            );
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(actionOutput)
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

    private static final class GetCardsInHand extends Action {
        GetCardsInHand(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            ArrayNode actionOutput = MAPPER.valueToTree(
                    gameManager.getCardsInHand(getInput().getPlayerIdx())
                            .stream()
                            .map(JsonMapper::mapCard)
                            .toList()
            );
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(actionOutput)
                    .build();
        }
    }

    private static final class GetCardsOnTable extends Action {
        GetCardsOnTable(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            ArrayNode actionOutput = MAPPER.createArrayNode();
            gameManager.getCardsOnTable().forEach(row -> {
                        ArrayNode rowNode = MAPPER.valueToTree(row.stream()
                                .map(JsonMapper::mapPlayableCard)
                                .toList()
                        );
                        actionOutput.add(rowNode);
                    }
            );
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(actionOutput)
                    .build();
        }
    }

    private static final class GetPlayerMana extends Action {
        GetPlayerMana(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonNodeFactory.instance.numberNode(gameManager.getPlayerMana(
                            getInput().getPlayerIdx())))
                    .build();
        }
    }

    private static final class EndPlayerTurn extends Action {
        EndPlayerTurn(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager) {
            gameManager.endTurn();
            return ActionOutput.builder()
                    .type(ActionOutput.Type.NONE)
                    .build();
        }
    }
}
