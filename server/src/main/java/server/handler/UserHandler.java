package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;
import service.requestsponse.LoginRequest;

public class UserHandler {
    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public void register(Context ctx) throws DataAccessException {
        UserData user = gson.fromJson(ctx.body(), UserData.class);
        AuthData auth = userService.register(user);
        writeJson(ctx, auth);
    }

    public void login(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = userService.login(loginRequest);
        writeJson(ctx, auth);
    }

    public void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.json(gson.toJson(null));
        ctx.status(200);
    }

    private void writeJson(Context ctx, AuthData body) {
        ctx.status(200);
        ctx.json(gson.toJson(body));
    }

}
