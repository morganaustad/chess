package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;
import service.request.LoginRequest;

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
        writeJson(ctx, 200, auth);
    }

    public void login(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = userService.login(loginRequest);
        writeJson(ctx, 200, auth);
    }

    public void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        writeJson(ctx, 200, null);
    }

    private void writeJson(Context ctx, int status, AuthData body) {
        ctx.status(status);
        ctx.json(gson.toJson(body));
    }

}
