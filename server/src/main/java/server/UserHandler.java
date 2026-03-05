package server;

import io.javalin.http.Context;
import service.AuthService;
import service.UserService;

import model.RegisterRequest;
import model.RegisterResult;

public class UserHandler {

    private final AuthService authService;
    private final UserService userService;

    public UserHandler(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void UserHandler(Context ctx){
        try {
            // This using features of Javalin and GSON to take the body of the HTTP request (JSON) and turn it into a RegsiterRequest
            // In order for this to work the field names in RegisterRequest need to match the JSON keys in the body exactly
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);

            // The result is what we receive from the service
            RegisterResult res = userService.register(req);

            // Now we return the write HTTP code and the body
            ctx.status(200);
            ctx.json(res);
        }
        

    }

    
}
