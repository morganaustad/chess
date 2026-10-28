package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class RookMovesCalculator extends SlidingMovesCalculator {
    private final static int[][] OFFSETS = { {0, 1}, {0, -1}, {-1, 0}, {1, 0} };

    public RookMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position, OFFSETS);
    }
}
