package gwentstone;

import java.util.List;

public class TurnManager {
    private int currentPlayer;
    List<Player> players;

    public TurnManager(int startingPlayer, List<Player> players) {
        this.currentPlayer = startingPlayer;
        this.players = players;
    }

    public void endTurn() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }
}
