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

    /**
     * Check if a round is already in progress
     *
     * @return True if the round started, False otherwise
     */
    public boolean isRoundStarted() {
        return gameState.isRoundStarted();
    }

    /**
     * Signal the start of a new round.
     * This method invokes the routines that must run at the beginning of a round.
     *
     */
    public void startRound(){
        gameState.startRoundRoutine();
    }

    /**
     * Get the index of the active player.
     *
     * @return The index of the player (1 or 2)
     */
    public int getPlayerTurn(){
        // internally, we store the player index starting from zero
        return gameState.getTurnManager().getCurrentPlayerIdx() + 1;
    }
}
