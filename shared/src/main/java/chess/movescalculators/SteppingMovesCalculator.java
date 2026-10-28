package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SteppingMovesCalculator implements MovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;
    private final int[][] offsets;

    public SteppingMovesCalculator(ChessBoard board, ChessPosition position, int[][] offsets) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
        this.offsets = offsets;
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        List<ChessMove> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffset(offset[0], offset[1]);

            if (board.isEmpty(target) || board.isEnemyPresent(piece.getTeamColor(), target)) {
                moves.add(new ChessMove(position, target, null));
            }
        }

        return moves;
    }
}
