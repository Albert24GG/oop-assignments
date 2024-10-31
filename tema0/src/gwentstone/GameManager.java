package gwentstone;

import fileio.StartGameInput;

import java.util.List;

/**
 * Class containing the main API for interacting with the GameState.
 * It contains all the actions that get executed during a game.
 */
public final class GameManager {
    private final GameState gameState;

    public GameManager(final List<Player> players, final StartGameInput input) {
        gameState = new GameState(players, input);
    }
}
