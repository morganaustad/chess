package model.requestresponse;

import java.util.Collection;

public record ListGamesResult(Collection<GameSummary> games) {
}
