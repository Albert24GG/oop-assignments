package gwentstone;

import fileio.StartGameInput;
import gwentstone.cards.Deck;
import gwentstone.cards.impl.Hero;

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
     */
    public void startRound() {
        gameState.startRoundRoutine();
    }

    /**
     * Get the index of the active player.
     *
     * @return The index of the player (1 or 2)
     */
    public int getPlayerTurn() {
        // internally, we store the player index starting from zero
        return gameState.getTurnManager().getCurrentPlayerIdx() + 1;
    }

    /**
     * Get the deck assigned to a player in the current game
     *
     * @param playerIdx The player index (1 or 2)
     * @return The specified player's deck
     */
    public Deck getPlayerDeck(final int playerIdx) {
        // internally, we store the player index starting from zero
        return gameState.getPlayers().get(playerIdx - 1).getGameData().getCurrentDeck();
    }

    /**
     * Get the hero assigned to a player in the current game
     *
     * @param playerIdx The player index (1 or 2)
     * @return The specified player in the current game
     */
    public Hero getPlayerHero(final int playerIdx) {
        // internally, we store the player index starting from zero
        return gameState.getPlayers().get(playerIdx - 1).getGameData().getCurrentHero();
    }

    /**
     * End current's player turn
     */
    public void endTurn() {
        TurnManager turnManager = gameState.getTurnManager();
        if (turnManager.isRoundEndable()) {
            gameState.endRoundRoutine();
            gameState.startRoundRoutine();
        }
        turnManager.endTurn();
    }
}
