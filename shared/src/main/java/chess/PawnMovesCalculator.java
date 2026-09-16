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

        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        ChessPosition oneForward = position.addOffSet(direction, 0);
        ChessPosition twoForward = position.addOffSet(direction * 2, 0);

        int[][] takeOffsets = { {direction, 1}, {direction, -1} };

        ChessPiece.PieceType[] promotionTypes = {
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP
        };


        if (board.isEmpty(oneForward)) {
            boolean promoted = false;

            // Check first move
            if (position.getRow() == startRow && board.isEmpty(twoForward)) {
                moves.add(new ChessMove(position, twoForward, null));
            }

            // Check Promotion
            if (oneForward.getRow() == promotionRow) {
                for (ChessPiece.PieceType type : promotionTypes) {
                    moves.add(new ChessMove(position, oneForward, type));
                }
                promoted = true;
            }

            // Add move
            if (!promoted) {
                moves.add(new ChessMove(position, oneForward, null));
            }
        }

        for (int[] offset : takeOffsets) {
            ChessPosition target = position.addOffSet(offset[0], offset[1]);

            if (board.isEnemyPresent(target, piece.getTeamColor())) {
                // Check promotions
                if (target.getRow() == promotionRow) {
                    for (ChessPiece.PieceType type : promotionTypes) {
                        moves.add(new ChessMove(position, target, type));
                    }
                } else {
                    moves.add(new ChessMove(position, target, null));
                }
            }
        }

        return moves;
    }
}
