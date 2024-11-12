package org.poo.gwentstone.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
import org.poo.gwentstone.GameManager;
import org.poo.gwentstone.utils.JsonMapper;

import java.util.Map;
import java.util.function.Function;

public final class ActionFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<ActionsInput, Action>> ACTIONS = Map.ofEntries(
            Map.entry("getPlayerTurn", GetPlayerTurn::new),
            Map.entry("getPlayerDeck", GetPlayerDeck::new),
            Map.entry("getPlayerHero", GetPlayerHero::new),
            Map.entry("endPlayerTurn", EndPlayerTurn::new),
            Map.entry("getCardsInHand", GetCardsInHand::new),
            Map.entry("getCardsOnTable", GetCardsOnTable::new),
            Map.entry("getPlayerMana", GetPlayerMana::new),
            Map.entry("getCardAtPosition", GetCardAtPosition::new),
            Map.entry("placeCard", PlaceCard::new),
            Map.entry("cardUsesAttack", CardUsesAttack::new),
            Map.entry("cardUsesAbility", CardUsesAbility::new),
            Map.entry("useAttackHero", UseAttackHero::new),
            Map.entry("useHeroAbility", UseHeroAbility::new),
            Map.entry("getFrozenCardsOnTable", GetFrozenCardsOnTable::new),
            Map.entry("getTotalGamesPlayed", GetTotalGamesPlayed::new),
            Map.entry("getPlayerOneWins", actionsInput -> new GetPlayerWins(actionsInput, 0)),
            Map.entry("getPlayerTwoWins", actionsInput -> new GetPlayerWins(actionsInput, 1))

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
        public ActionOutput execute(final GameManager gameManager) {
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

    private static final class CardUsesAttack extends Action {
        CardUsesAttack(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            var actionOutput = ActionOutput.builder();
            try {
                gameManager.cardUsesAtack(getInput().getCardAttacker(),
                        getInput().getCardAttacked());
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

    private static final class CardUsesAbility extends Action {
        CardUsesAbility(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            var actionOutput = ActionOutput.builder();
            try {
                gameManager.cardUsesAbility(getInput().getCardAttacker(),
                        getInput().getCardAttacked());
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

    private static final class UseAttackHero extends Action {
        UseAttackHero(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            var actionOutput = ActionOutput.builder();
            try {
                actionOutput.type(ActionOutput.Type.NONE);
                gameManager.useAttackHero(getInput().getCardAttacker()).ifPresent(winner -> {
                            String outputString =
                                    "Player " + (winner == 0 ? "one" : "two")
                                            + " killed the enemy hero.";
                            actionOutput.type(ActionOutput.Type.GAME_ENDED)
                                    .actionOutput(JsonNodeFactory.instance.textNode(outputString));
                        }
                );
                return actionOutput.build();
            } catch (final ActionException err) {
                return actionOutput
                        .type(ActionOutput.Type.ERROR)
                        .actionInput(getInput())
                        .actionOutput(JsonNodeFactory.instance.textNode(err.getMessage()))
                        .build();
            }
        }
    }

    private static final class UseHeroAbility extends Action {
        UseHeroAbility(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            var actionOutput = ActionOutput.builder();
            try {
                gameManager.useHeroAbility(getInput().getAffectedRow());
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

    private static final class GetFrozenCardsOnTable extends Action {
        GetFrozenCardsOnTable(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            ArrayNode actionOutput = MAPPER.createArrayNode();
            gameManager.getFrozenCardsOnTable()
                    .forEach(row -> actionOutput.add(JsonMapper.mapPlayableCard(row)));
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(actionOutput)
                    .build();
        }
    }

    private static final class GetPlayerTurn extends Action {
        GetPlayerTurn(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
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
        public ActionOutput execute(final GameManager gameManager) {
            ArrayNode actionOutput = MAPPER.valueToTree(
                    gameManager.getPlayerDeck(getInput().getPlayerIdx() - 1)
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
        public ActionOutput execute(final GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonMapper.mapPlayableCard(
                            gameManager.getPlayerHero(getInput().getPlayerIdx() - 1)))
                    .build();
        }
    }

    private static final class GetCardsInHand extends Action {
        GetCardsInHand(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            ArrayNode actionOutput = MAPPER.valueToTree(
                    gameManager.getCardsInHand(getInput().getPlayerIdx() - 1)
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
        public ActionOutput execute(final GameManager gameManager) {
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
        public ActionOutput execute(final GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonNodeFactory.instance.numberNode(gameManager.getPlayerMana(
                            getInput().getPlayerIdx() - 1)))
                    .build();
        }
    }

    private static final class GetCardAtPosition extends Action {
        GetCardAtPosition(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            var output =
                    ActionOutput.builder().type(ActionOutput.Type.OUTPUT).actionInput(getInput());
            try {
                Coordinates coords = new Coordinates();
                coords.setX(getInput().getX());
                coords.setY(getInput().getY());
                return output
                        .actionOutput(
                                JsonMapper.mapPlayableCard(gameManager.getCardAtPosition(coords)))
                        .build();
            } catch (final ActionException err) {
                return output
                        .actionOutput(JsonNodeFactory.instance.textNode(err.getMessage()))
                        .build();
            }
        }
    }

    private static final class EndPlayerTurn extends Action {
        EndPlayerTurn(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            gameManager.endTurn();
            return ActionOutput.builder()
                    .type(ActionOutput.Type.NONE)
                    .build();
        }
    }

    private static final class GetTotalGamesPlayed extends Action {
        GetTotalGamesPlayed(final ActionsInput input) {
            super(input);
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(
                            JsonNodeFactory.instance.numberNode(gameManager.getTotalGamesPlayed()))
                    .build();
        }
    }

    private static final class GetPlayerWins extends Action {
        private final int playerIdx;

        GetPlayerWins(final ActionsInput input, final int playerIdx) {
            super(input);
            this.playerIdx = playerIdx;
        }

        @Override
        public ActionOutput execute(final GameManager gameManager) {
            return ActionOutput.builder()
                    .type(ActionOutput.Type.OUTPUT)
                    .actionInput(getInput())
                    .actionOutput(JsonNodeFactory.instance.numberNode(
                            gameManager.getPlayerWins(playerIdx)))
                    .build();
        }
    }
}
