package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }


    @Override
    public Collection<ChessMove> pieceMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = { {1, 1}, {1, -1}, {-1, -1}, {-1, 1} };

        for (int[] offset : offsets) {
            ChessPosition target = position.addOffSet(offset[0], offset[1]);
            while (board.isValidMove(target, piece.getTeamColor())) {
                moves.add(new ChessMove(position, target, null));
                target = target.addOffSet(offset[0], offset[1]);
            }
        }
        return moves;
    }
}
