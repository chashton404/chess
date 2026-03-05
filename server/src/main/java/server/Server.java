// Because I wanted to keep using VSCode instead of intelliJ, we learned how to use the 
package server;

// Import the javalin stuff
import io.javalin.*;
import io.javalin.http.Context;

// Import the services being used
import service.*;

// Import the DAOS and local storage
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class Server {

    // Initialize Javalin
    private final Javalin javalin;

    // Initialize the DAOs
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    // Initialize the services
    private final AuthService authService = new AuthService(authDAO);
    private final GameService gameService = new GameService(gameDAO);
    private final UserService userService = new UserService(userDAO, authDAO);

    // Initialize the handlers
    private final ClearHandler clearHandler = new ClearHandler(authService, gameService, userService);
    private final UserHandler userHandler = new UserHandler(userService);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", clearHandler::clear)
            .post("/user", userHandler::registerUser);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
