package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class BishopMovesCalculator extends SlidingMovesCalculator {
    private final static int[][] OFFSETS = { {1, 1}, {1, -1}, {-1, -1}, {-1, 1} };

    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position, OFFSETS);
    }
}
