// Because I wanted to keep using VSCode instead of intelliJ, we learned how to use the 
package server;

// Import the javalin stuff
import io.javalin.*;

// Import the services being used
import service.*;

// Import the DAOS and local storage
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import dataaccess.DatabaseManager;

//import what is needed for the websocket
import websocket.WebSocketHandler;


public class Server {
    // Initialize the DAOS
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    // Initialize the Web Socket Handler
    private final WebSocketHandler webSocketHandler;
    
    // Initialize Javalin
    private final Javalin javalin;
    

    public Server() {
        boolean useSQL = true;

        // Initialize the DAOs
        if (useSQL) {
            try {
                DatabaseManager.configureDatabase();
                userDAO = new SQLUserDAO();
                authDAO = new SQLAuthDAO();
                gameDAO = new SQLGameDAO();
            } catch (DataAccessException e) {
                System.err.println(String.format("Database Setup FAILED %s", e.getMessage()));
                System.exit(1);
            }
        } else {
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
        }

        // Initialize the Services
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);

        // Initialize the Handlers
        ClearHandler clearHandler = new ClearHandler(authService, gameService, userService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        // Initialize the Web Socket
        this.webSocketHandler = new WebSocketHandler(gameService);

        javalin = Javalin.create(config -> {config.staticFiles.add("web"); config.jsonMapper(new GsonJsonMapper());})
            .delete("/db", clearHandler::clear)
            .post("/user", userHandler::registerUser)
            .post("/session", userHandler::login)
            .delete("/session", userHandler::logout)
            .post("/game", gameHandler::createGame)
            .get("/game", gameHandler::listGames)
            .put("/game", gameHandler::joinGame)
            .ws("/ws", ws -> {
                ws.onConnect(webSocketHandler::handleConnect);
                ws.onMessage(webSocketHandler::handleMessage);
                ws.onClose(webSocketHandler::handleClose);
            });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
