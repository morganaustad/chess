package chess;

import java.util.Collection;

public interface MovesCalculator {
    public Collection<ChessMove> getPossibleMoves();
}
