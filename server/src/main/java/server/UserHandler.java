package server;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import io.javalin.http.Context;
import service.AuthService;
import service.UserService;

import model.RegisterRequest;
import model.RegisterResult;

import java.util.Map;

public class UserHandler {

    private final AuthService authService;
    private final UserService userService;

    public UserHandler(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void registerUser(Context ctx){
        try {
            // This using features of Javalin and GSON to take the body of the HTTP request (JSON) and turn it into a RegsiterRequest
            // In order for this to work the field names in RegisterRequest need to match the JSON keys in the body exactly
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);

            // The result is what we receive from the service
            RegisterResult res = userService.register(req);

            // Now we return the write HTTP code and the body
            ctx.status(200);
            ctx.json(res);
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
