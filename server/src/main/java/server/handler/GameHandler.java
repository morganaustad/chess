package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requestsponse.CreateGameRequest;
import service.requestsponse.JoinGameRequest;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    public void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        ctx.json(gameService.listGames(authToken));
        ctx.status(200);
    }

    public void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        CreateGameRequest createRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);
        ctx.json(gameService.createGame(authToken, createRequest));
        ctx.status(200);
    }

    public void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        JoinGameRequest joinRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        gameService.joinGame(authToken, joinRequest);
        ctx.json(Map.of());
        ctx.status(200);
    }
}
