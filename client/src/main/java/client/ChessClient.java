package client;

import exception.ResponseException;

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
    private static String status = "[LOGGED_OUT]";

    // Sub-REPLS (Read-Eval-Print-Loop)
    private final SignedOutREPL signedOutREPL;
    private final SignedInREPL signedInREPL;
    private final InGameREPL inGameREPL;

    // Initialize the ChessClient
    public ChessClient(String serverUrl) throws ResponseException {
        this.server = new ServerFacade(serverUrl);
        this.signedOutREPL = new SignedOutREPL(this, server);
        this.signedInREPL = new SignedInREPL(this, server);
        this.inGameREPL = new InGameREPL(this, server);
    }

    // Important Setters and Getters
    public void setAuthToken(String token) { this.authToken = token; }
    public void setState(State s) {this.state = s;}

    public String getAuthToken() {
        return authToken;
    }

    // The beginning of the REPL
    public void run() {
        System.out.println( "\n" + FIRE + SET_TEXT_COLOR_RED + " WELCOME TO THE DOPEST LITTEST CHESS SERVER " + FIRE);
        
        // We initialize a scanner as that helps us to read input from different sources
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (state == State.SIGNEDIN) {
                status = "[LOGGED IN]";
            } else if (state == State.SIGNEDOUT) {
                status = "[LOGGED OUT]";
            } else if (state == State.INGAME) {
                status = "[GAMEPLAY]";
            } else {
                status = "[LOGGED OUT]";
            }

            // Prompt for input
            printPrompt();
            // The scanner object allows us to read the whole line that we get from the user
            String line = scanner.nextLine();

            // Then work through our different REPLs and output what is returned
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

    // This is simply the function that prints to prompt the user for input
    public static void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + status + " >>> " + SET_TEXT_COLOR_GREEN);
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
                    case State.INGAME -> inGameREPL.help();
                };
            }

            if (cmd.equals("quit") && state == State.SIGNEDOUT) {
                return "quit";
            }

            return switch (state) {
                case State.SIGNEDOUT -> signedOutREPL.signedOutReponses(cmd, params);
                case State.SIGNEDIN -> signedInREPL.signedInResponses(cmd, params);
                case State.INGAME -> inGameREPL.inGameResponses(cmd, params);
            };

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
