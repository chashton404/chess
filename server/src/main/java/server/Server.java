// Because I wanted to keep using VSCode instead of intelliJ, we learned how to use the 
package server;

import io.javalin.*;
import io.javalin.http.Context;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final AuthService authService = new AuthService();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        .post("/session", this::loginUser)
        .delete("/db", this::clearData)
        .post("/user", this::registerUser)

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private String loginUser(Context ctx) {
        
    }



    private void clearData(Context ctx) {
        authService.clearAuth();
        gameService.clearGames();
        userService.clearUsers();

        ctx.status(200);
    }
}
