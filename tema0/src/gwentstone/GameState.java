package gwentstone;

import fileio.StartGameInput;
import gwentstone.utils.InputParser;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public final class GameState {
    private final List<Player> players;

    @Getter(AccessLevel.PACKAGE)
    private TurnManager turnManager;
    private GameBoard gameBoard;
    @Getter
    private boolean roundStarted = false;

    /**
     * Routine called at the start of the round.
     */
    public void startRoundRoutine() {
        roundStarted = true;
    }

    public GameState(final List<Player> players, final StartGameInput input) {
        this.players = players;

        // Initialize the state of the players
        players.get(0).initializeGameData(input.getPlayerOneDeckIdx(), input.getShuffleSeed(),
                InputParser.parseHero(input.getPlayerOneHero()));
        players.get(1).initializeGameData(input.getPlayerTwoDeckIdx(), input.getShuffleSeed(),
                InputParser.parseHero(input.getPlayerTwoHero()));

        turnManager = new TurnManager(input.getStartingPlayer(), players);
        gameBoard = new GameBoard();
    }
}
