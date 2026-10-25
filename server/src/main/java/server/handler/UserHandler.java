package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws DataAccessException {
        Gson gson = new Gson();
        UserData user = gson.fromJson(ctx.body(), UserData.class);
        AuthData auth = userService.register(user);
        writeJson(ctx, 200, auth);
    }

    private void writeJson(Context ctx, int status, AuthData body) {
        Gson gson = new Gson();

        ctx.status(status);
        ctx.json(gson.toJson(body));
    }

}
