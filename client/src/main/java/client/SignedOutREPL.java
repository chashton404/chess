package client;

import java.util.Arrays;

import exception.ResponseException;
import ui.EscapeSequences.*;
import server.ServerFacade;

import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;

public class SignedOutREPL {
    private final ServerFacade server;
    private final ChessClient client;

    public SignedOutREPL(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
    }
    
    public String signedOutReponses(String cmd, String[] params) {
        try {
            return switch(cmd) {
                case "login" -> loginUser(params);
                case "register" -> registerUser(params); 
                default -> help(); 
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
        
    }

    public String loginUser(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            AuthData authData = server.loginUser(new LoginRequest(username, password));

            client.setAuthToken(authData.authToken());
            client.setState(State.SIGNEDIN);

            return String.format("Logged in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String registerUser(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            AuthData authData = server.registerUser(new RegisterRequest(username, password, email));

            client.setAuthToken(authData.authToken());
            client.setState(State.SIGNEDIN);

            return String.format("Logged in as %s.", username);
            
        }
        throw new ResponseException(400, "Expected; <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String help() {
        return "     register <USERNAME> <PASSWORD> <EMAIL> - to create an account" +
                "\n     login <USERNAME> <PASSWORD> - to play chess" + 
                "\n     quit - playing chess" +
                "\n     help - with possible commands";
    }
}
