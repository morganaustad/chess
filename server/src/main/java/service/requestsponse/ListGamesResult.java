package service.requestsponse;

import java.util.Collection;

public record ListGamesResult(Collection<GameSummary> games) {
}
