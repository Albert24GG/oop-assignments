package gwentstone;

import fileio.StartGameInput;
import gwentstone.utils.InputParser;

import java.util.List;

public class GameState {
    private final List<Player> players;
    private TurnManager turnManager;
    private GameBoard gameBoard;

    public GameState(List<Player> players, StartGameInput input) {
        this.players = players;

        // Initialize the state of the players
        players.get(0).initializeGameData(input.getPlayerOneDeckIdx(), input.getShuffleSeed(), InputParser.parseHero(input.getPlayerOneHero()));
        players.get(1).initializeGameData(input.getPlayerTwoDeckIdx(), input.getShuffleSeed(), InputParser.parseHero(input.getPlayerTwoHero()));

        turnManager = new TurnManager(input.getStartingPlayer(), players);
        gameBoard = new GameBoard();
    }

}
