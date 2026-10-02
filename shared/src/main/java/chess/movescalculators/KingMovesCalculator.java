package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements MovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    public KingMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = { {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1} };

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffset(offset[0], offset[1]);

            if (board.isEmpty(target) || board.isEnemyPresent(piece.getTeamColor(), target)) {
                moves.add(new ChessMove(position, target, null));
            }
        }

        return moves;
    }
}
