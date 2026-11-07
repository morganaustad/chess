package ui;

import chess.ChessBoard;
import chess.ChessGame;

import static ui.EscapeSequences.*;

public class BoardRenderer {
    private static final String BORDER_BG = SET_BG_COLOR_WHITE;
    private static final String BORDER_TEXT = SET_TEXT_COLOR_BLACK;


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



    private static String borderCell(String label) {
        return " " + label + " ";
    }
}