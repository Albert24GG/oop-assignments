package gwentstone;

import java.util.List;

public final class TurnManager {
    private int currentPlayer;
    private final List<Player> players;

    public TurnManager(final int startingPlayer, final List<Player> players) {
        this.currentPlayer = startingPlayer;
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
}
