package client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.CreateGameRequest;
import model.ListGamesResult;
import model.ListGameData;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // Login User Tests
    @Test
    public void goodLogin() throws Exception {

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        facade.logoutUser(auth.authToken());

        AuthData loginAuth = facade.loginUser(new LoginRequest("donkey", "kong"));
        assertTrue(loginAuth.authToken().length() > 10, "AuthKey should be returned");
    }

    @Test
    public void badLogin() throws Exception {
        
        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        facade.logoutUser(auth.authToken());

        assertThrows(Exception.class, () -> facade.loginUser(new LoginRequest("donkey", "king")), 
                    "Invalid Password should lead to unsuccessful login");
    }

    // Register User Tests
    @Test
    public void goodRegister() throws Exception{

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        assertTrue(auth.authToken().length() > 10, "AuthKey should be returned");
        
    }

    @Test
    public void badRegister() throws Exception {

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));

        assertThrows(Exception.class, () -> facade.registerUser(new RegisterRequest("donkey", "king", "123")),
                    "User should not be able to register with existing username");
    }


    // Logout User Tests
    @Test
    public void goodLogout() throws Exception{

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        
        facade.logoutUser(auth.authToken());

    }

    @Test
    public void badLogout() throws Exception {
        
        String authToken = "1234";
        assertThrows(Exception.class, () -> facade.logoutUser(authToken), "Unable to logout with invalid token");

    }

    // Create Game Tests
    @Test
    public void goodCreateGame() throws Exception {

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        CreateGameResult result = facade.createGame(new CreateGameRequest("chase"), auth.authToken());

        assertTrue(result.gameID() > 0, "GameID should be returned");

    }

    @Test
    public void badCreateGame() throws Exception {
        
        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest(null), auth.authToken()));


    }   

    // List Game Tests
    @Test
    public void goodListGames() throws Exception{

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        facade.createGame(new CreateGameRequest("chase"), auth.authToken());

        ListGamesResult games = facade.listGames(auth.authToken());
        assertTrue(games.games().size() > 0, "Games list should be greater than 0" );
        
        
    }

    @Test
    public void badListGames() {
        assertThrows(Exception.class, () -> facade.listGames(null));
    }

    // Join Game Tests
    @Test
    public void goodJoinGame() throws Exception{

        AuthData auth = facade.registerUser(new RegisterRequest("donkey", "kong", "123"));
        CreateGameResult game = facade.createGame(new CreateGameRequest("chase"), auth.authToken());

        facade.joinGame(new JoinGameRequest("WHITE", game.gameID()), auth.authToken());

        ListGamesResult games = facade.listGames(auth.authToken());

        ListGameData firstGame = games.games().iterator().next();
        assertTrue(firstGame.whiteUsername().equals("donkey"),"White username should match input" );

    }

    @Test
    public void badJoinGame() {
        assertThrows(Exception.class, () -> facade.joinGame(null, null));
    }

}
