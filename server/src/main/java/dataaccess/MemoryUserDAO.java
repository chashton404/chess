package dataaccess;

import java.util.HashMap;
import model.UserData;

public class MemoryUserDAO {
    
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData u) throws DataAccessException {
        // First we check that the UserData doesn't have any null values, if they do then we raise the Exception
        validUserData(u);

        // Now that we know that there are no null values we can add the UserData to the local storage
        users.put(u.username(), u);
    }

    public boolean checkUser(UserData u) throws DataAccessException{
        // Check to verify that the UserData is valid data
        validUserData(u);

        // Return whether or not user exists
        Boolean userExists = users.containsKey(u.username());
        return userExists;
    }

    public void clearUsers() {
        users.clear();
    }

    private void validUserData(UserData u) throws DataAccessException {
        if (u == null) {
            throw new DataAccessException("Error: Null UserData");
        } else if (u.username() == null) {
            throw new DataAccessException("Error: Missing Username");
        } else if (u.password() == null) {
            throw new DataAccessException("Error: Missing Password");
        } else if (u.email() == null) {
            throw new DataAccessException("Error: Missing Email");
        }
    }

}
