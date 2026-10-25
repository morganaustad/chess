package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import server.handler.ClearHandler;
import server.handler.GameHandler;
import server.handler.UserHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(authDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);
        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

        clearHandler = new ClearHandler(clearService);
        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);

        registerRoutes(javalin);
        registerExceptions(javalin);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerRoutes(Javalin javalin) {
        javalin.delete("/db", clearHandler::clear);
        javalin.delete("/session", userHandler::logout);

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.post("/game", gameHandler::createGame);

        javalin.put("/game", gameHandler::joinGame);

        javalin.get("/game", gameHandler::listGames);
    }

    private void registerExceptions(Javalin javalin) {
        javalin.exception(DataAccessException.class, this::dataException);
        javalin.exception(Exception.class, this::otherException);
        javalin.exception(BadRequestException.class, this::badRequestException);
        javalin.exception(AlreadyTakenException.class, this::alreadyTakenException);
        javalin.exception(UnauthorizedException.class, this::unauthorizedException);
    }

    private String gsonMapping(String input1, String input2) {
        Gson gson = new Gson();
        return gson.toJson(Map.of(input1, input2));
    }

    private void dataException(DataAccessException ex, Context ctx) {
        String json = gsonMapping("message", "Error: " + ex.getMessage());

        ctx.status(500);
        ctx.json(json);
    }

    private void otherException(Exception ex, Context ctx) {
        String json = gsonMapping("message", "Error: Something went wrong");

        ctx.status(500);
        ctx.json(json);
    }

    private void badRequestException(BadRequestException ex, Context ctx) {
        String json = gsonMapping("message", "Error: " + ex.getMessage());

        ctx.status(400);
        ctx.json(json);
    }

    private void alreadyTakenException(AlreadyTakenException ex, Context ctx) {
        String json = gsonMapping("message", "Error: " + ex.getMessage());

        ctx.status(403);
        ctx.json(json);
    }

    private void unauthorizedException(UnauthorizedException ex, Context ctx) {
        String json = gsonMapping("message", "Error: " + ex.getMessage());

        ctx.status(401);
        ctx.json(json);
    }
}
