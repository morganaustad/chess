package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMovesCalculator implements MovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    RookMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = { {0, 1}, {0, -1}, {-1, 0}, {1, 0} };

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffset(offset[0], offset[1]);
            boolean canMove = true;

            while (canMove) {
                if (board.isEmpty(target)) {
                    moves.add(new ChessMove(position, target, null));
                    target = target.addOffset(offset[0], offset[1]);
                } else if (board.isEnemyPresent(piece.getTeamColor(), target)) {
                    moves.add(new ChessMove(position, target, null));
                    canMove = false;
                } else {
                    canMove = false;
                }
            }
        }

        return moves;
    }
}
