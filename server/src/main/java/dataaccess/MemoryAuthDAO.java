package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData a) throws DataAccessException {
        // Make sure that it's a valid input
        validAuth(a);

        // Add the new input
        auths.put(a.authToken(), a);
    }

    public void clearAuth(){
        auths.clear();
    }

    private void validAuth(AuthData a) throws DataAccessException {
        if (a == null) {
            throw new DataAccessException("Error: Null AuthData"); 
        } else if (a.authToken() == null) {
            throw new DataAccessException("Error: Null AuthToken");
        } else if (a.username() == null) {
            throw new DataAccessException("Error: Null Username");
        }
    }

    @Override
    public String getAuth() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public void deleteAuth() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }
}
