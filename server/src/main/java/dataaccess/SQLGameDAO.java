package dataaccess;

import java.util.Collection;

import model.ListGameData;

public class SQLGameDAO implements GameDAO {

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public Collection<ListGameData> listGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public Boolean checkGame(Integer gameID) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkGame'");
    }

    @Override
    public Boolean checkColor(Integer gameID, String playerColor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkColor'");
    }

    @Override
    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public void clearGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearGames'");
    }
    
}
