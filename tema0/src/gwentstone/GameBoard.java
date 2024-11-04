package gwentstone;

import fileio.Coordinates;
import gwentstone.cards.impl.PlayableMinion;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameBoard {
    private static final int BOARD_WIDTH = 5;
    private static final int BOARD_HEIGHT = 4;
    @Getter(AccessLevel.PACKAGE)
    private List<List<PlayableMinion>> board =
            IntStream.range(0, BOARD_HEIGHT)
                    .mapToObj(i -> new ArrayList<PlayableMinion>(BOARD_WIDTH))
                    .collect(Collectors.toList());
    // 2D List that indicates if a card has attacked/used its ability this round
    private List<List<Boolean>> attackedThisRound =
            IntStream.range(0, BOARD_HEIGHT)
                    .mapToObj(i -> new ArrayList<Boolean>(Collections.nCopies(BOARD_WIDTH, false)))
                    .collect(Collectors.toList());

    /**
     * Mapping between the possible placement and the rows corresponding to them
     */
    private static final Map<PlayableMinion.Placement, List<Integer>> ROWS_PLACEMENT = Map.of(
            PlayableMinion.Placement.BACK, List.of(3, 0),
            PlayableMinion.Placement.FRONT, List.of(2, 1)
    );

    /**
     * Get the row index associated with a player and a minion placement
     *
     * @param playerIdx index of the player (0 / 1)
     * @param placement placement of the minion (BACK / FRONT)
     * @return the row index
     */
    private int getRowIdx(final int playerIdx, final PlayableMinion.Placement placement) {
        return ROWS_PLACEMENT.get(placement).get(playerIdx);
    }


    /**
     * Get the rows belonging to a given player.
     *
     * @param playerIdx The index of the player
     * @return The list of rows
     */
    private List<List<PlayableMinion>> getPlayerRows(final int playerIdx) {
        return IntStream.range(0, BOARD_HEIGHT)
                .filter(i -> getPlayerIdxHoldingRow(i) == playerIdx)
                .mapToObj(i -> board.get(i))
                .collect(Collectors.toList());
    }

    /**
     * Get a row of minions from the game board.
     *
     * @param rowIdx The index of the row
     * @return The row as a list of minions
     */
    public List<PlayableMinion> getRow(final int rowIdx) {
        return board.get(rowIdx);
    }

    /**
     * Check if a minion can be placed on the board
     *
     * @param playerIdx index of the player (0 / 1)
     * @param minion    the minion in question
     * @return whether the card can be placed or not
     */
    public boolean canPlace(final int playerIdx, final PlayableMinion minion) {
        return board.get(getRowIdx(playerIdx, minion.getPlacement())).size() != BOARD_WIDTH;
    }


    /**
     * Place the minion on tha game board
     *
     * @param playerIdx index of the player
     * @param minion    the minion to place
     */
    public void placeMinion(final int playerIdx, final PlayableMinion minion) {
        if (!canPlace(playerIdx, minion)) {
            return;
        }
        board.get(getRowIdx(playerIdx, minion.getPlacement())).add(minion);
    }

    /**
     * Unfreeze the cards of a given player.
     *
     * @param playerIdx The index of the player
     */
    public void unfreezePlayerCards(final int playerIdx) {
        IntStream.range(0, BOARD_HEIGHT)
                .filter(i -> getPlayerIdxHoldingRow(i) == playerIdx)
                .mapToObj(i -> board.get(i))
                .forEach(row -> row.forEach(PlayableMinion::unfreeze));
    }

    /**
     * Clear the attack history of a given player.
     *
     * @param playerIdx The index of the player
     */
    void resetPlayerAttackHistory(final int playerIdx) {
        IntStream.range(0, BOARD_HEIGHT)
                .filter(i -> getPlayerIdxHoldingRow(i) == playerIdx)
                .mapToObj(i -> attackedThisRound.get(i))
                .forEach(row -> row.replaceAll(e -> false));
    }

    /**
     * Mark a given card as having attacked in the current round.
     *
     * @param coords Attacker's coordinates on the game board
     */
    void markAttacker(final Coordinates coords) {
        attackedThisRound.get(coords.getX()).set(coords.getY(), true);
    }

    /**
     * Find the index of the player that a given card belongs to.
     *
     * @param coords Card's coordinates on the game board
     * @return The index of the player
     */
    public int getPlayerIdxHoldingCard(final Coordinates coords) {
        return getPlayerIdxHoldingRow(coords.getX());
    }

    /**
     * Find the index of the player that a given row of cards belongs to.
     *
     * @param rowIdx The index of the row
     * @return The index of the player
     */
    public int getPlayerIdxHoldingRow(final int rowIdx) {
        return rowIdx < BOARD_HEIGHT / 2 ? 1 : 0;
    }

    /**
     * Check if a given card has attacked this round
     *
     * @param coords Card's coordinates on the game board
     * @return True if the card has attacked, false otherwise
     */
    public boolean attackedThisRound(final Coordinates coords) {
        return attackedThisRound.get(coords.getX()).get(coords.getY());
    }

    /**
     * Get the card at given coordinates
     *
     * @param coords Coordinates of the requested card
     * @return The minion card at the specified coordinates
     */
    PlayableMinion getCard(final Coordinates coords) {
        return board.get(coords.getX()).get(coords.getY());
    }

    /**
     * Check if a player has any tanks on the game board.
     *
     * @param playerIdx Player's index (0 or 1)
     * @return True if the player has tanks, false otherwise
     */
    boolean hasTanksOnBoard(final int playerIdx) {
        // tanks can be found only in the front row
        return board.get(BOARD_HEIGHT / 2 - playerIdx)
                .stream()
                .anyMatch(PlayableMinion::isTank);
    }

    /**
     * Remove a card from the game board.
     *
     * @param coords Card's coordinates on the game board
     */
    void removeCard(final Coordinates coords) {
        board.get(coords.getX()).remove(coords.getY());
    }

    /**
     * Get the list of frozen cards on the table
     *
     * @return A list of frozen cards
     */
    List<PlayableMinion> getFrozenCards() {
        return board.stream()
                .flatMap(List::stream)
                .filter(PlayableMinion::isFrozen)
                .collect(Collectors.toList());
    }

}
