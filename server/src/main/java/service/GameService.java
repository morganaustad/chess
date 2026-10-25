package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import service.exception.BadRequestException;
import service.requestsponse.CreateGameRequest;
import service.requestsponse.CreateGameResult;
import service.requestsponse.GameSummary;
import service.requestsponse.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;

import static service.VerifyService.verifyAuth;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        verifyAuth(authToken, authDAO);
        Collection<GameData> games = gameDAO.listGames();
        Collection<GameSummary> summaries = new ArrayList<>();

        for (GameData game : games) {
            summaries.add(new GameSummary(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return new ListGamesResult(summaries);
    }

    public CreateGameResult CreateGame(String authToken, CreateGameRequest gameRequest) throws DataAccessException {
        verifyAuth(authToken, authDAO);

        if (
                gameRequest == null
                || gameRequest.gameName() == null
        ) {
            throw new BadRequestException("Bad Create Game Request");
        }

        int id = gameDAO.insertGame(
                new GameData(
                        0,
                        null,
                        null,
                        gameRequest.gameName(),
                        new ChessGame()
                )
        );

        return new CreateGameResult(id);
    }
}
