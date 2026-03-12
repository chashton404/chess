package dataaccess;

import model.UserData;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    public void createUser(UserData u) throws DataAccessException {
        // Hash the password
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());

        // User question marks to avoid SQL injection
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, u.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, u.email());

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Failed to create user: %s", e.getMessage()));
        }
    }

    public Boolean checkUser(String username) throws DataAccessException {
        // User the question mark to avoid SQL injection
        var statment = "SELECT EXISTS(SELECT 1 FROM user WHERE username = ?)";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statment)){
            preparedStatement.setString(1, username);

            // Use a try block so the connection gets closed
            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to search for user: %s", e.getMessage()));
        }
        return false;
    }

    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);
            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return new UserData(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("email"));
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to get user: %s", e.getMessage()));
        }
        return new UserData(null, null, null);
    }


    public void clearUsers() throws DataAccessException {
        var statement = "DELETE FROM user";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to clear user table: %s", e.getMessage()));
        }
    }
    
}
