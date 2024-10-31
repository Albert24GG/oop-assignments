package gwentstone;

import gwentstone.cards.impl.PlayableMinion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameBoard {
    private static final int BOARD_WIDTH = 5;
    private static final int BOARD_HEIGHT = 4;
    private List<List<PlayableMinion>> board =
            IntStream.range(0, BOARD_HEIGHT)
                    .mapToObj(i -> new ArrayList<PlayableMinion>(BOARD_WIDTH))
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
}
