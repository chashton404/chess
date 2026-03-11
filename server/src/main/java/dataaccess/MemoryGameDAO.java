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

    public Boolean checkGame(Integer gameID) {
        return games.containsKey(gameID);
    }

    @Override
    public void getGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public void updateGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }
    
}
