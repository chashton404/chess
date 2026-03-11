package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UnauthorizedException;
import dataaccess.DataAccessException;
import dataaccess.BadRequestException;
import model.CreateGameResult;
import model.JoinGameRequest;
import model.CreateGameRequest;
import model.ListGamesResult;
import model.JoinGameRequest;

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

    public void joinGame(JoinGameRequest req) {
        
        //Check to validate the request
        if ()

    }
}
