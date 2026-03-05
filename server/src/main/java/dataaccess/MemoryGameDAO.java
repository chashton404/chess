package dataaccess;

import model.GameData;
import dataaccess.GameDAO;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void clearGames() {
        games.clear();
    }

    @Override
    public void createGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public void getGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public void listGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void updateGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }
    
}
