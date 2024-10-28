package gwentstone;

import gwentstone.cards.Minion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameBoard {
    private static final int BOARD_WIDTH = 5;
    private static final int BOARD_HEIGHT = 4;
    private List<List<Minion>> board = IntStream.range(0, BOARD_HEIGHT).mapToObj(i -> new ArrayList<Minion>(BOARD_WIDTH)).collect(Collectors.toList());
}
