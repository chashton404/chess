package client;

import exception.ResponseException;

import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

import java.util.ArrayList;

import server.ServerFacade;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.ListGamesResult;
import model.ListGameData;
import model.JoinGameRequest;

public class SignedInREPL {
    private final ServerFacade server;
    private final ChessClient client;
    private ArrayList<ListGameData> localGameList;
    
    public SignedInREPL(ChessClient client, ServerFacade server) {
        this.server = server;
        this.client = client;
    }

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

    private String createGame(String... params) throws ResponseException {
        if (params.length > 0) {
            String gameName = params[0];
            CreateGameResult game = server.createGame(new CreateGameRequest(gameName), client.getAuthToken());

            return String.format("Game successfully created", game.gameID());
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

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

    private String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            Integer gameNum = Integer.parseInt(params[0]);
            String playerColor = params[1];

            // make sure that game actually exists
            if (gameNum < 1 || gameNum > localGameList.size()) {
                throw new ResponseException(400, "You absolute bafoon, the game must exist to join it");
            }

            // get the gameID from the game number
            int gameID = localGameList.get(gameNum - 1).gameID();

            server.joinGame(new JoinGameRequest(playerColor, gameID), client.getAuthToken());
            return "Successfully Joined Game";

        }
        throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
    }

    private String observeGame(String... params) throws ResponseException {
        return "Successfully observing game";
    }   

    private String logoutUser() throws ResponseException {
        server.logoutUser(client.getAuthToken());
        client.setState(State.SIGNEDOUT);
        client.setAuthToken(null);
        return "Logout Successful";
    }


    public String help() {
        return SET_TEXT_COLOR_BLUE + "     create <NAME>" + SET_BG_COLOR_DARK_GREY + " - a game" +
                SET_TEXT_COLOR_BLUE + "\n     list -" + SET_BG_COLOR_DARK_GREY + " games" +
                SET_TEXT_COLOR_BLUE + "\n     join <ID> [WHITE|BLACK]" + SET_BG_COLOR_DARK_GREY + " - a game" + 
                SET_TEXT_COLOR_BLUE + "\n     observe <ID>" + SET_BG_COLOR_DARK_GREY + " - a game" + 
                SET_TEXT_COLOR_BLUE + "\n     logout" + SET_BG_COLOR_DARK_GREY + " - when you are done" +
                SET_TEXT_COLOR_BLUE + "\n     quit" + SET_BG_COLOR_DARK_GREY + " - playing chess" +
                SET_TEXT_COLOR_BLUE + "\n     help" + SET_BG_COLOR_DARK_GREY + " - with possible commands";
    }
}
