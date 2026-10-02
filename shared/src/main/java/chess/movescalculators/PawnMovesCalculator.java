package chess.movescalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements MovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        List<ChessMove> moves = new ArrayList<>();

        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        ChessPosition oneForward = position.addOffset(direction, 0);
        ChessPosition twoForward = position.addOffset(direction * 2, 0);

        int[][] takeOffsets = { {direction, 1}, {direction, -1} };

        ChessPiece.PieceType[] promotionTypes = {
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
        };

        if (board.isEmpty(oneForward)) {
            // Start row
            if (position.getRow() == startRow && board.isEmpty(twoForward)) {
                moves.add(new ChessMove(position, twoForward, null));
            }

            // Promotion
            boolean promoted = false;
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

        for (int[] takeOffset : takeOffsets) {
            ChessPosition target = position.addOffset(takeOffset[0], takeOffset[1]);

            if (board.isEnemyPresent(piece.getTeamColor(), target)) {
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
