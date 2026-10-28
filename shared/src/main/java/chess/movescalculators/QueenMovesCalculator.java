package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class QueenMovesCalculator extends SlidingMovesCalculator {
    private final static int[][] OFFSETS = { {1, 1}, {1, -1}, {-1, -1}, {-1, 1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0} };

    public QueenMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position, OFFSETS);
    }

}
