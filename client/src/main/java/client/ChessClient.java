package client;

import exception.ResponseException;

import java.util.Scanner;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ChessClient implements NotificationHandler{

    // Useful Variables
    private String authToken = null;
    private ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private static String status = "[LOGGED_OUT]";
    private ChessGame currentGame;
    private String currentColor;
    private Integer currentGameID;

    // Sub-REPLS (Read-Eval-Print-Loop)
    private final SignedOutREPL signedOutREPL;
    private final SignedInREPL signedInREPL;
    private final InGameREPL inGameREPL;

    // Create the WebSocketFacade, and assign it as null for now
    private WebSocketFacade ws = null;

    // Initialize the ChessClient
    public ChessClient(String serverUrl) throws ResponseException {
        //We use the serverUrl to create the ws connection
        this.serverUrl = serverUrl;

        // Initialize the server
        this.server = new ServerFacade(serverUrl);

        // Initialize each of the REPLS
        this.signedOutREPL = new SignedOutREPL(this, server);
        this.signedInREPL = new SignedInREPL(this, server);
        this.inGameREPL = new InGameREPL(this, server);
    }

    // Important Setters and Getters
    public void setAuthToken(String token) { 
        this.authToken = token;
    }

    public void setState(State s) {
        this.state = s;
    }

    public State getState() {
        return state;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public WebSocketFacade getWebSocket() {
        return ws;
    }

    public void updateGame(ChessGame game, String playerColor) {
        this.currentGame = game;
        this.currentColor = playerColor;
    }

    public void updateGameID(Integer gameID) {
        this.currentGameID = gameID;
    }

    public ChessGame getLocalGame() {
        return currentGame;
    }

    public String getLocalColor() {
        return currentColor;
    }

    public Integer getLocalGameID() {
        return currentGameID;
    }

    // The beginning of the REPL
    public void run() {
        System.out.println( "\n" + FIRE + SET_TEXT_COLOR_RED + " WELCOME TO THE DOPEST LITTEST CHESS SERVER " + FIRE);
        
        // We initialize a scanner as that helps us to read input from different sources
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!"quit".equals(result)) {
            if (state == State.SIGNEDIN) {
                status = "[LOGGED IN]";
            } else if (state == State.SIGNEDOUT) {
                status = "[LOGGED OUT]";
            } else if (state == State.INGAME) {
                status = "[GAMEPLAY]";
            } else if (state == State.OBSERVER) {
                status = "[OBSERVING]";
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

    // This is how we decide which REPL is being used currently
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
                    case State.OBSERVER -> inGameREPL.help();
                };
            }

            if (cmd.equals("quit") && state == State.SIGNEDOUT) {
                return "quit";
            }

            return switch (state) {
                case State.SIGNEDOUT -> signedOutREPL.signedOutReponses(cmd, params);
                case State.SIGNEDIN -> signedInREPL.signedInResponses(cmd, params);
                case State.INGAME -> inGameREPL.inGameResponses(cmd, params);
                case State.OBSERVER -> inGameREPL.inGameResponses(cmd, params);
            };

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    // -------- MORE WEBSOCKET STUFF -------------------------------------
    // Command to Open the websocket
    public WebSocketFacade initializeWebSocket() throws ResponseException {
        if (ws == null) {
            ws = new WebSocketFacade(serverUrl, this);
        }
        return ws;
    }


    // Overrides for the Notification Handler
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                updateGame(loadGameMessage.getGame(), loadGameMessage.getPlayerColor());
                System.out.println(DrawBoard.drawBoard(loadGameMessage.getGame(), loadGameMessage.getPlayerColor(), null, null));
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println("Error: " + errorMessage.getErrorMessage());

            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getMessage());
            }
        }
        // Print the "[GAMEPLAY] >>>"" part again
        ChessClient.printPrompt();
    }

}
