package server;

import io.javalin.http.Context;
import service.*;

import java.util.Map;


public class ClearHandler {

    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public ClearHandler(AuthService authService, GameService gameService, UserService userService) {
        this.authService = authService;
        this.gameService = gameService;
        this.userService = userService;
    }

    public void clear(Context ctx) {
        try {
            gameService.clearGames();
            authService.clearAuth();
            userService.clearUsers();
    
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        } 
    }
}
