package client;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

import model.GameData;

public class ChessClient {

    // Useful Variables
    private String authToken = null;
    private ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String status = "[LOGGED_OUT]";

    // Sub-REPLS
    private final SignedOutREPL signedOutREPL;
    private final SignedInREPL signedInREPL;

    public ChessClient(String serverUrl) throws ResponseException {
        this.server = new ServerFacade(serverUrl);
        this.signedOutREPL = new SignedOutREPL(this, server);
        this.signedInREPL = new SignedInREPL(this, server);
    }

    // Important Setters and Getters
    public void setAuthToken(String token) { this.authToken = token; }
    public void setState(State s) {this.state = s;}

    public String getAuthToken() {
        return authToken;
    }

    public void run() {
        System.out.println( "\n" + FIRE + SET_TEXT_COLOR_RED + " WELCOME TO THE DOPEST LITTEST CHESS SERVER " + FIRE);
        
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (state == State.SIGNEDIN) {
                status = "[LOGGED IN]";
            } else {
                status = "[LOGGED OUT]";
            }
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + status + " >>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.trim().split("\\s+");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (cmd.isBlank() || cmd.equals("help")) {
                return switch (state) {
                    case State.SIGNEDOUT -> signedOutREPL.help();
                    case State.SIGNEDIN -> signedInREPL.help();
                };
            }

            if (cmd.equals("quit")) {
                return "quit";
            }

            return switch (state) {
                case State.SIGNEDOUT -> signedOutREPL.signedOutReponses(cmd, params);
                case State.SIGNEDIN -> signedInREPL.signedInResponses(cmd, params);
            };

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
