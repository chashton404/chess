package client;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_DARK_GREY;

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

            return String.format(RESET_TEXT_COLOR + "Logged in as %s.", username);
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

        return  SET_TEXT_COLOR_BLUE + "     register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_DARK_GREY + " - to create an account" +
                SET_TEXT_COLOR_BLUE + "\n     login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_DARK_GREY + "- to play chess" + 
                SET_TEXT_COLOR_BLUE + "\n     quit " + SET_TEXT_COLOR_DARK_GREY + "- playing chess" +
                SET_TEXT_COLOR_BLUE + "\n     help " + SET_TEXT_COLOR_DARK_GREY + "- with possible commands";
    }
}
