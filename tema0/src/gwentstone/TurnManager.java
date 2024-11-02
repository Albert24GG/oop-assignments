package gwentstone;

import java.util.List;

public final class TurnManager {
    private int currentPlayer;
    private final int startingPlayer;
    private final List<Player> players;

    public TurnManager(final int startingPlayer, final List<Player> players) {
        this.currentPlayer = startingPlayer;
        this.startingPlayer = startingPlayer;
        this.players = players;
    }

    /**
     * End the current turn and reset the player who moves next.
     */
    public void endTurn() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public int getCurrentPlayerIdx() {
        return currentPlayer;
    }

    public int getInactivePlayerIdx() {
        return (currentPlayer + 1) % players.size();
    }

    /**
     * Check if the round can be ended
     *
     * @return Boolean indicating whether the current round can be ended
     */
    boolean isRoundEndable() {
        return currentPlayer == (startingPlayer + players.size() - 1) % players.size();
    }
}
