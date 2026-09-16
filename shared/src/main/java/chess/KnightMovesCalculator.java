package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = { {2, -1}, {2, 1}, {-1, 2}, {1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2} };

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffSet(offset[0], offset[1]);
            if (board.isValidMove(target, piece.getTeamColor())) {
                moves.add(new ChessMove(position, target, null));
            }
        }
        return moves;
    }
}
