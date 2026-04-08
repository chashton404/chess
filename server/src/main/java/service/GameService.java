package service;

import dataaccess.GameDAO;
import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.UnauthorizedException;
import dataaccess.DataAccessException;
import dataaccess.BadRequestException;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.CreateGameRequest;
import model.ListGamesResult;
import model.ConnectionResult;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clearGames() throws DataAccessException {
        gameDAO.clearGames();
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req)
        throws BadRequestException, UnauthorizedException, DataAccessException {

        // Make sure that the request isn't null
        if (req.gameName() == null || authToken == null) {
            throw new BadRequestException("Error: bad request");
        }
        
        // Make sure that the auth exists in the database
        Boolean authExists = authDAO.checkAuth(authToken);

        if (authExists == false) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            Integer gameID = gameDAO.createGame(req.gameName());
            return new CreateGameResult(gameID);
        }

    }

    public ListGamesResult listGames(String authToken) 
        throws UnauthorizedException, DataAccessException {
        
        // Make sure that the request isn't null
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (!authDAO.checkAuth(authToken)){
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            ListGamesResult games = new ListGamesResult(gameDAO.listGames());
            return games;
        }

    }

    public void joinGame(String authToken, JoinGameRequest req) 
        throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        
        //Check to validate the request
        if (req == null || req.gameID() == null || req.playerColor() == null || authToken == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (!(req.playerColor().equals("WHITE") || req.playerColor().equals("BLACK"))) {
            throw new BadRequestException("Error: bad request");
        }

        // Check the authToken is valid
        if (!authDAO.checkAuth(authToken)){
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Check the game exists
        if (!gameDAO.checkGame(req.gameID())){
            throw new BadRequestException("Error: bad request");
        }

        // Verify that the color spot is empty
        if (!gameDAO.checkColor(req.gameID(), req.playerColor())) {
            throw new AlreadyTakenException("Error: already taken");
        }

        // get the username for the given authKey
        String username = authDAO.getUser(authToken);
        gameDAO.updateGame(req.gameID(), req.playerColor(), username);

    }

    public ConnectionResult connectGame(String authToken, Integer gameID) throws UnauthorizedException, BadRequestException, DataAccessException {
        // Check that neither the authToken nor the gameID is null
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (gameID == null) {
            throw new BadRequestException("Error: Game doesn't exist");
        }

        // Check that the authToken is valid
        if (!authDAO.checkAuth(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Check the game exists
        if (!gameDAO.checkGame(gameID)){
            throw new BadRequestException("Error: bad request");
        }

        // Get the username and the playerColor
        String username = authDAO.getUser(authToken);

        // Determine the userColor
        GameData gameData = gameDAO.getGame(gameID);
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String userColor;

        if (username.equals(whiteUsername)) {
            userColor = "WHITE";
        } else if (username.equals(blackUsername)){
            userColor = "BLACK";
        } else {
            userColor = null;
        }

        // Get the ChessGame
        ChessGame chessGame = gameData.game();

        return new ConnectionResult(username, userColor, chessGame);

    }
}
