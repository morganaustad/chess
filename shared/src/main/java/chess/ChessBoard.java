package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece[] square : squares) {
            if (square[0] != null) {
                square[0] = null;
            }
        }

        // Add pawns
        for (int i = 1; i <= 8; i++) {
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // Add back row
        ChessGame.TeamColor[] colors = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        for (ChessGame.TeamColor color : colors) {
            int row = (color == ChessGame.TeamColor.WHITE) ? 1 : 8;
            this.addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            this.addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            this.addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            this.addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            this.addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
            this.addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            this.addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            this.addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        }
    }


    public boolean isValidMove(ChessPosition target, ChessGame.TeamColor teamColor) {
        if (!target.isInBounds()) {
            return false;
        }
        ChessPiece targetOccupant = this.getPiece(target);
        if (targetOccupant == null) {
            return true;
        } else return !targetOccupant.getTeamColor().equals(teamColor);
    }


    public boolean isEmpty(ChessPosition target) {
        if (!target.isInBounds()) {
            return false;
        }

        return this.getPiece(target) == null;
    }


    public boolean isEnemyPresent(ChessPosition target, ChessGame.TeamColor teamColor) {
        if (!target.isInBounds()) {
            return false;
        }
        ChessPiece targetOccupant = this.getPiece(target);
        if (targetOccupant == null) {
            return false;
        } else if (targetOccupant.getTeamColor() != teamColor) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        for (ChessPiece[] pieceList : squares) {
            for (ChessPiece piece : pieceList) {
                if (piece != null) {
                    value.append(piece.toString());
                    value.append(", ");
                }
            }
        }
        return new String(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
