package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.requestsponse.*;

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

    public CreateGameResult createGame(String authToken, CreateGameRequest gameRequest) throws DataAccessException {
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

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws DataAccessException {
        AuthData auth = verifyAuth(authToken, authDAO);

        if (
                joinRequest == null
                || joinRequest.playerColor() == null
        ) {
            throw new BadRequestException("Bad Join Game Request");
        }

        GameData game = gameDAO.getGame(joinRequest.gameID());

        if (game == null) { throw new BadRequestException("Bad Join Game Get Game Request"); }

        GameData updatedGame = claimColor(game, joinRequest.playerColor(), auth.username());
        gameDAO.updateGame(updatedGame);
    }

    private GameData claimColor(GameData game, ChessGame.TeamColor requestedColor, String username) {
        if (requestedColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("White already taken");
            }

            return new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Black already taken");
            }

            return new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()
            );
        }
    }
}
