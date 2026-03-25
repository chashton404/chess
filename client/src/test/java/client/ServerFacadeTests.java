package client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;
import model.CreateGameResult;
import model.CreateGameRequest;;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public static void clear() throws Exception {
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
        CreateGameResult result = facade.createGame(new CreateGameRequest("chase"), auth.authToken());
    }   

    // List Game Tests
    @Test
    public void goodListGames() {

        

    }

    @Test
    public void badListGames() {
        
    }

    // Join Game Tests
    @Test
    public void goodJoinGame() {

    }

    @Test
    public void badJoinGame() {
        
    }

}
