package gwentstone;

import java.util.List;

final class TurnManager {
    private int currentPlayer;
    private final int startingPlayer;
    private final List<Player> players;

    TurnManager(final int startingPlayer, final List<Player> players) {
        this.currentPlayer = startingPlayer;
        this.startingPlayer = startingPlayer;
        this.players = players;
    }

    /**
     * End the current turn and reset the player who moves next.
     */
    void endTurn() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    Player getInactivePlayer() {
        return players.get(getInactivePlayerIdx());
    }

    int getCurrentPlayerIdx() {
        return currentPlayer;
    }

    int getInactivePlayerIdx() {
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
