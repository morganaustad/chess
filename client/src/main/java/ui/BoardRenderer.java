package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardRenderer {
    private static final String BORDER_BG = SET_BG_COLOR_WHITE;
    private static final String BORDER_TEXT = SET_TEXT_COLOR_BLACK;
    private static final String LIGHT_BG = SET_BG_COLOR_YELLOW;
    private static final String DARK_BG = SET_BG_COLOR_MAGENTA;
    private static final String WHITE_PIECE_TEXT = SET_TEXT_COLOR_WHITE;
    private static final String BLACK_PIECE_TEXT = SET_TEXT_COLOR_BLACK;

    public static String render(ChessBoard board, ChessGame.TeamColor perspective) {
        boolean whiteView = perspective != ChessGame.TeamColor.BLACK;

        int[] rows = whiteView ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] cols = whiteView ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};

        StringBuilder output = new StringBuilder();
        output.append(headerRows(cols));
        for (int row : rows) {
            output.append(boardRow(board, row, cols));
        }
        output.append(headerRows(cols));

        return output.toString();
    }

    private static String headerRows(int[] cols) {
        StringBuilder line = new StringBuilder();
        line.append(BORDER_BG).append(BORDER_TEXT).append(EMPTY);

        for (int col : cols) {
            char letter = (char)('a' + col - 1);
            line.append(borderCell(String.valueOf(letter)));
        }

        line.append(EMPTY);
        line.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
        return line.toString();
    }

    private static String boardRow(ChessBoard board, int row, int[] cols) {
        StringBuilder line = new StringBuilder();
        line.append(BORDER_BG).append(BORDER_TEXT).append(borderCell(String.valueOf(row)));

        for (int col : cols) {
            line.append(square(board, row, col));
        }

        line.append(BORDER_BG).append(BORDER_TEXT).append(borderCell(String.valueOf(row)));
        line.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
        return line.toString();
    }

    private static String borderCell(String label) {
        return " " + label + " ";
    }

    private static String square(ChessBoard board, int row, int col) {
        String bg = isLight(row, col) ? LIGHT_BG : DARK_BG;
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return bg + EMPTY;
        }
        return bg + textColorFor(piece) + symbolFor(piece);
    }

    private static boolean isLight(int row, int col) {
        return (row + col) % 2 == 1;
    }

    private static String textColorFor(ChessPiece piece) {
        return ChessGame.TeamColor.WHITE == piece.getTeamColor() ? WHITE_PIECE_TEXT : BLACK_PIECE_TEXT;
    }

    private static String symbolFor(ChessPiece piece) {
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> white ? WHITE_KING : BLACK_KING;
            case QUEEN -> white ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> white ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> white ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}