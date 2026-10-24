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
import io.javalin.http.Context;
import server.handler.ClearHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(authDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);
        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin.delete("/db", clearHandler::clear);

        javalin.exception(DataAccessException.class, this::dataException);
        javalin.exception(Exception.class, this::otherException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void dataException(DataAccessException ex, Context ctx) {
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message:", "Error: " + ex.getMessage()));

        ctx.status(500);
        ctx.json(json);
    }

    private void otherException(Exception ex, Context ctx) {
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message:", "Error: Something went wrong"));

        ctx.status(500);
        ctx.json(json);
    }
}
