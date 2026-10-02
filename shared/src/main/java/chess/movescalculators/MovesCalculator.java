package chess.movescalculators;

import chess.ChessMove;

import java.util.Collection;

public interface MovesCalculator {
    Collection<ChessMove> getPossibleMoves();
}
