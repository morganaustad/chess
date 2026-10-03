package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return null; }

        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove pieceMove : pieceMoves) {
            ChessBoard copy = new ChessBoard(board);

            applyMove(copy, pieceMove);

            if (!isInCheck(copy, piece.getTeamColor())) {
                validMoves.add(pieceMove);
            }
        }

        return validMoves;
    }


    private void applyMove(ChessBoard board, ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(),
                move.hasPromotion() ? new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()) : piece);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        if (board.getPiece(startPos) == null) {
            throw new InvalidMoveException("Invalid Move: no piece at starting position");
        }

        if (board.getPiece(startPos).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid Move: not this piece's turn");
        }

        if (!validMoves(startPos).contains(move)) {
            throw new InvalidMoveException("Invalid Move: not a valid move");
        }

        applyMove(board, move);
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(this.board, teamColor);
    }


    private boolean isInCheck(ChessBoard board, TeamColor teamColor) {
        PieceAndLocation kingPiece = board.findPiece(ChessPiece.PieceType.KING, teamColor);
        if (kingPiece == null) {
            return false;
        }

        Collection<PieceAndLocation> enemyTeamPieces = getTeamPieces(
                board,
                (TeamColor.WHITE == teamColor) ? TeamColor.BLACK : TeamColor.WHITE
        );
        List<ChessMove> enemyTeamMoves = new ArrayList<>();

        for (PieceAndLocation piece : enemyTeamPieces) {
            enemyTeamMoves.addAll(piece.boardPiece().pieceMoves(board, piece.boardPosition()));
        }

        boolean inCheck = false;
        for (ChessMove move : enemyTeamMoves) {
            if (move.getEndPosition().equals(kingPiece.boardPosition())) {
                inCheck = true;
                break;
            }
        }

        return inCheck;
    }


    private Collection<PieceAndLocation> getTeamPieces(ChessBoard board, TeamColor teamColor) {
        List<PieceAndLocation> teamPieces = new ArrayList<>();
        ChessPiece boardPiece;
        ChessPosition boardPosition;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                boardPosition = new ChessPosition(row, col);
                boardPiece = board.getPiece(boardPosition);

                if (boardPiece != null && boardPiece.getTeamColor() == teamColor) {
                    teamPieces.add(new PieceAndLocation(boardPiece, boardPosition));
                }
            }
        }

        return teamPieces;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInStalemate(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        PieceAndLocation kingPiece = board.findPiece(ChessPiece.PieceType.KING, teamColor);
        Collection<PieceAndLocation> enemyTeamPieces = getTeamPieces(
                (TeamColor.WHITE == teamColor) ? TeamColor.BLACK : TeamColor.WHITE
        );
        List<ChessMove> enemyTeamMoves = new ArrayList<>();
        List<ChessPosition> enemyTeamMovesEndPos = new ArrayList<>();
        List<ChessMove> kingMoves = new ArrayList<>(kingPiece.boardPiece().pieceMoves(board, kingPiece.boardPosition()));
        List<ChessPosition> kingMovesEndPos = new ArrayList<>();

        for (PieceAndLocation piece : enemyTeamPieces) {
            enemyTeamMoves.addAll(piece.boardPiece().pieceMoves(board, piece.boardPosition()));
        }

        for (ChessMove enemyMove : enemyTeamMoves) {
            enemyTeamMovesEndPos.add(enemyMove.getEndPosition());
        }

        for (ChessMove kingMove : kingMoves) {
            kingMovesEndPos.add(kingMove.getEndPosition());
        }

        for (ChessPosition enemyMoveEndPos : enemyTeamMovesEndPos) {
            kingMovesEndPos.remove(enemyMoveEndPos);
        }

        return kingMovesEndPos.isEmpty();
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }


    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
