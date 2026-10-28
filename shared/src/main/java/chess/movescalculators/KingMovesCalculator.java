package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class KingMovesCalculator extends SteppingMovesCalculator {
    private final static int[][] OFFSETS = { {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1} };

    public KingMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position, OFFSETS);
    }
}
