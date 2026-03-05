package server;

import io.javalin.http.Context;
import service.AuthService;
import service.UserService;

import model.RegisterRequest;
import model.RegisterResult;

public class RegisterHandler {

    private final AuthService authService;
    private final UserService userService;

    public RegisterHandler(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void registerUser(Context ctx){
        try {
            // This using features of Javalin and GSON to take the body of the HTTP request (JSON) and turn it into a RegsiterRequest
            // In order for this to work the field names in RegisterRequest need to match the JSON keys in the body exactly
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            
        }
        

    }

    
}
