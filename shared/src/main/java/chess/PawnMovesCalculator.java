package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }


    @Override
    public Collection<ChessMove> pieceMoves() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsetsWhite = { {1, 0}, {1, 1}, {1, -1} };
        int[][] offsetsBlack = { {-1, 0}, {-1, 1}, {-1, -1} };

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            for (int[] offset : offsetsWhite) {
                return List.of();
            }
        } else {
            for (int[] offset : offsetsBlack) {
                return List.of();
            }
        }

        return moves;
    }
}
