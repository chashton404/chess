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

    public Boolean checkAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException(("Error: Null AuthToken"));
        }

        Boolean authExists = auths.containsKey(authToken);
        return authExists;
    }

    public Boolean checkUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException(("Error: Null AuthToken"));
        }
        return false;
    }

    public void deleteAuth(String authKey) throws DataAccessException, UnauthorizedException {
        if (authKey == null) {
            throw new DataAccessException("Error: authKey is null");
        }

        AuthData removedAuth = auths.remove(authKey);
        if (removedAuth == null || !authKey.equals(removedAuth.authToken())) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public String getUser(String authToken) throws DataAccessException {
        return auths.get(authToken).username();
    }
}
