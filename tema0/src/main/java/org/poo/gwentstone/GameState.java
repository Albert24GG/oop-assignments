package org.poo.gwentstone;

import lombok.Getter;
import org.poo.fileio.StartGameInput;
import org.poo.gwentstone.utils.InputParser;

import java.util.List;

@Getter
final class GameState {
    private final List<Player> players;

    private final TurnManager turnManager;
    private final GameBoard gameBoard;
    private boolean gameEnded = false;

    /**
     * Routine called at the start of the round.
     */
    void startRoundRoutine() {
        if (gameEnded) {
            return;
        }
        players.forEach(p -> {
            PlayerGameData gameData = p.getGameData();

            gameData.startRoundRoutine();
            gameData.resetUsedHeroAbility();
        });
    }

    /**
     * End the current game
     */
    void endGame() {
        gameEnded = true;
    }

    GameState(final List<Player> players, final StartGameInput input) {
        this.players = players;

        // Initialize the state of the players
        players.get(0).initializeGameData(input.getPlayerOneDeckIdx(), input.getShuffleSeed(),
                InputParser.parseHero(input.getPlayerOneHero()));
        players.get(1).initializeGameData(input.getPlayerTwoDeckIdx(), input.getShuffleSeed(),
                InputParser.parseHero(input.getPlayerTwoHero()));

        turnManager = new TurnManager(input.getStartingPlayer() - 1, players);
        gameBoard = new GameBoard();

        startRoundRoutine();
    }
}
