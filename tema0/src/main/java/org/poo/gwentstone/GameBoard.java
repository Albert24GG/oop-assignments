package org.poo.gwentstone;

import lombok.Getter;
import org.poo.fileio.Coordinates;
import org.poo.gwentstone.cards.impl.PlayableMinion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class GameBoard {
    private static final int BOARD_WIDTH = 5;
    private static final int BOARD_HEIGHT = 4;
    @Getter
    private List<List<PlayableMinion>> board =
            IntStream.range(0, BOARD_HEIGHT)
                    .mapToObj(i -> new ArrayList<PlayableMinion>(BOARD_WIDTH))
                    .collect(Collectors.toList());
    /**
     * 2D List that indicates if a card has attacked/used its ability this round
     */
    private final List<List<Boolean>> attackedThisRound =
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

    private int getRowIdx(final int playerIdx, final PlayableMinion.Placement placement) {
        return ROWS_PLACEMENT.get(placement).get(playerIdx);
    }

    List<PlayableMinion> getRow(final int rowIdx) {
        return board.get(rowIdx);
    }

    boolean canPlace(final int playerIdx, final PlayableMinion minion) {
        return board.get(getRowIdx(playerIdx, minion.getPlacement())).size() != BOARD_WIDTH;
    }

    void placeMinion(final int playerIdx, final PlayableMinion minion) {
        if (!canPlace(playerIdx, minion)) {
            return;
        }
        board.get(getRowIdx(playerIdx, minion.getPlacement())).add(minion);
    }

    void unfreezePlayerCards(final int playerIdx) {
        IntStream.range(0, BOARD_HEIGHT)
                .filter(i -> getPlayerIdxHoldingRow(i) == playerIdx)
                .mapToObj(i -> board.get(i))
                .forEach(row -> row.forEach(PlayableMinion::unfreeze));
    }

    void resetPlayerAttackHistory(final int playerIdx) {
        IntStream.range(0, BOARD_HEIGHT)
                .filter(i -> getPlayerIdxHoldingRow(i) == playerIdx)
                .mapToObj(attackedThisRound::get)
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

    int getPlayerIdxHoldingCard(final Coordinates coords) {
        return getPlayerIdxHoldingRow(coords.getX());
    }

    int getPlayerIdxHoldingRow(final int rowIdx) {
        return rowIdx < BOARD_HEIGHT / 2 ? 1 : 0;
    }

    boolean attackedThisRound(final Coordinates coords) {
        return attackedThisRound.get(coords.getX()).get(coords.getY());
    }

    PlayableMinion getCard(final Coordinates coords) {
        return board.get(coords.getX()).get(coords.getY());
    }

    boolean hasTanksOnBoard(final int playerIdx) {
        // tanks can be found only in the front row
        return board.get(BOARD_HEIGHT / 2 - playerIdx)
                .stream()
                .anyMatch(PlayableMinion::isTank);
    }

    void removeCard(final Coordinates coords) {
        board.get(coords.getX()).remove(coords.getY());
    }

    List<PlayableMinion> getFrozenCards() {
        return board.stream()
                .flatMap(List::stream)
                .filter(PlayableMinion::isFrozen)
                .collect(Collectors.toList());
    }
}
