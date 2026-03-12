package dataaccess;

import model.GameData;
import model.ListGameData;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;

public class MemoryGameDAO implements GameDAO {
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    Integer gameID = 1;

    public void clearGames() {
        games.clear();
    }

    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException("Error: Game Name is Null");
        }

        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());

        games.put(gameID, newGame);
        Integer oldGameID = gameID;
        gameID += 1;

        return oldGameID;
    }

    public Collection<ListGameData> listGames() {
        Collection<ListGameData> list = new ArrayList<>();
        for (GameData game : games.values()) {
            ListGameData newListItem = new ListGameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            list.add(newListItem);
        }
        return list;
    }

    public Boolean checkGame(Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("Error: gameID is null");
        }

        return games.containsKey(gameID);
    }

    public Boolean checkColor(Integer gameID, String playerColor) {
        GameData game = games.get(gameID);
       
        if (playerColor.equals("WHITE")) {
            if (!(game.whiteUsername() == null)) {
                return false;
            } else {
                return true;
            }

        } else if (playerColor.equals("BLACK")) {
            if (!(game.blackUsername() == null)) {
                return false;
            } else {
                return true;
            }
        }

        // for invalid types return false
        return false;
    }

    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {
        if (playerColor.equals("WHITE")) {
            GameData updatedGame = games.get(gameID).updateWhite(username);
            games.put(gameID, updatedGame);
        } else if (playerColor.equals("BLACK")) {
            GameData updatedGame = games.get(gameID).updateBlack(username);
            games.put(gameID, updatedGame);
        }
    }
    
}
