package client;

import exception.ResponseException;

import java.util.ArrayList;

import server.ServerFacade;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesResult;
import model.ListGameData;

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
        return "Successfully joined game";
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
        return "     create <NAME> - a game" +
                "\n     list - games" +
                "\n     join <ID> [WHITE|BLACK] - a game" + 
                "\n     observe <ID> - a game" + 
                "\n     logout - when you are done" +
                "\n     quit - playing chess" +
                "\n     help - with possible commands";
    }
}
