package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    KingMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1} };

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffSet(offset[0], offset[1]);
            if (board.isValidMove(target, piece.getTeamColor())) {
                moves.add(new ChessMove(position, target, null));
            }
        }
        return moves;
    }
}
