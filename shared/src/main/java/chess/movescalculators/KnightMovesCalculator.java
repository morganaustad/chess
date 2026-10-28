package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class KnightMovesCalculator extends SteppingMovesCalculator {
    private final static int[][] OFFSETS = { {2, 1}, {2, -1}, {1, -2}, {1, 2}, {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2} };

    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position, OFFSETS);
    }
}
