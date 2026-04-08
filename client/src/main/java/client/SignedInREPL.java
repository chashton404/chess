package client;

import exception.ResponseException;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

import java.util.ArrayList;

import model.CreateGameRequest;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.ListGamesResult;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import model.ListGameData;
import model.JoinGameRequest;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

public class SignedInREPL implements NotificationHandler {
    private final ServerFacade server;
    private final ChessClient client;
    private ArrayList<ListGameData> localGameList;
    
    public SignedInREPL(ChessClient client, ServerFacade server) {
        this.server = server;
        this.client = client;
    }

    // note that here cmd is the first word and params are all the words following them
    public String signedInResponses(String cmd, String[] params) {
        try {
            return switch(cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logoutUser();
                default -> help(); 
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } 
    }

    // Matches pattern 'create <name>'
    private String createGame(String... params) throws ResponseException {
        if (params.length > 0) {
            String gameName = params[0];
            CreateGameResult game = server.createGame(new CreateGameRequest(gameName), client.getAuthToken());

            return String.format("Game successfully created", game.gameID());
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    // Matches pattern 'list'
    private String listGames() throws ResponseException {
        ListGamesResult gameData = server.listGames(client.getAuthToken());
        var games = gameData.games();

        if (games.isEmpty()) {
            return "No games found, use `create <NAME>` to start one.";
        }

        
        this.localGameList = new ArrayList<>(games);

        StringBuilder gamesList = new StringBuilder();
        int index = 1;

        for (ListGameData game : games) {
            gamesList.append(String.format("%d. %s - White: %s, Black: %s\n",
                    index++,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "[OPEN]",
                    game.blackUsername() != null ? game.blackUsername() : "[OPEN]"));
            
        }
        return gamesList.toString();
    }

    // Matches pattern 'join <gameNum> [BLACK|WHITE]'
    private String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            try {
                Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
            }

            Integer gameNum = Integer.parseInt(params[0]);
            String playerColor = params[1];

            ListGamesResult gameData = server.listGames(client.getAuthToken());
            var games = gameData.games();

            if (games.isEmpty()) {
                return "No games found, use `create <NAME>` to start one.";
            }

            this.localGameList = new ArrayList<>(games);

            // make sure that game actually exists
            if (gameNum < 1 || gameNum > localGameList.size()) {
                throw new ResponseException(400, "You absolute bafoon, the game must exist to join it");
            }

            // get the gameID from the game number
            int gameID = localGameList.get(gameNum - 1).gameID();

            // Create a new game for now
            ChessGame game = new ChessGame();

            // Communicate with the server to join the game
            server.joinGame(new JoinGameRequest(playerColor, gameID), client.getAuthToken());

            // Initialize the WebSocket Facade
            client.initializeWebSocket();

            WebSocketFacade ws = client.getWebSocket();
            ws.connect(client.getAuthToken(), gameID);

            client.setState(State.INGAME);
            return "Great, you joined the game. Now let's just hope the board shows up...";
        }
        throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
    }

    // Matches pattern 'observe <gameNum>'
    private String observeGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            try {
                Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
            }

            Integer gameNum = Integer.parseInt(params[0]);

            ListGamesResult gameData = server.listGames(client.getAuthToken());
            var games = gameData.games();
    
            if (games.isEmpty()) {
                return "No games found, use `create <NAME>` to start one.";
            }
    
            this.localGameList = new ArrayList<>(games);
    
            // make sure that game actually exists
            if (gameNum < 1 || gameNum > localGameList.size()) {
                throw new ResponseException(400, "You absolute bafoon, the game must exist to join it");
            }
            
            ChessGame game = new ChessGame();

            return DrawBoard.drawBoard(game);
        }
        throw new ResponseException(400, "Expected <ID>");
    }   

    // Matches pattern 'logout'
    private String logoutUser() throws ResponseException {
        server.logoutUser(client.getAuthToken());
        client.setState(State.SIGNEDOUT);
        client.setAuthToken(null);
        return "Logout Successful";
    }

    // Matches pattern 'help'
    public String help() {
        return SET_TEXT_COLOR_BLUE + "     create <NAME>" + SET_TEXT_COLOR_BLACK + " - a game" +
                SET_TEXT_COLOR_BLUE + "\n     list -" + SET_TEXT_COLOR_BLACK + " games" +
                SET_TEXT_COLOR_BLUE + "\n     join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_BLACK + " - a game" + 
                SET_TEXT_COLOR_BLUE + "\n     observe <ID>" + SET_TEXT_COLOR_BLACK + " - a game" + 
                SET_TEXT_COLOR_BLUE + "\n     logout" + SET_TEXT_COLOR_BLACK + " - when you are done" +
                SET_TEXT_COLOR_BLUE + "\n     quit" + SET_TEXT_COLOR_BLACK + " - playing chess" +
                SET_TEXT_COLOR_BLUE + "\n     help" + SET_TEXT_COLOR_BLACK + " - with possible commands";
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                DrawBoard.drawBoard(loadGameMessage.getGame());
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
