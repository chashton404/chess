package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{

    public void createAuth(AuthData a) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, a.authToken());
                preparedStatement.setString(2, a.username());

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Failed to add auth to db: %s", e.getMessage()));
        }
    }

    public Boolean checkAuth(String authToken) throws DataAccessException {
        // User the question mark to avoid SQL injection
        var statment = "SELECT EXISTS(SELECT 1 FROM auth WHERE authToken = ?)";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statment)){
            preparedStatement.setString(1, authToken);

            // Use a try block so the connection gets closed
            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to search for AuthToken: %s", e.getMessage()));
        }
        return false;
    }

    public Boolean checkUser(String username) throws DataAccessException {
        var statement = "SELECT EXISTS(SELECT 1 FROM auth where username = ?)";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to search for user in auth: %s", e.getMessage()));
        }
        return false;
    }

    @Override
    public String getUser(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {
        if(!checkAuth(authToken)){
            throw new UnauthorizedException("Error: unauthorized");
        }

        var statement = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)){
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error deleting the authToken: %s", e.getMessage()));
        }
    }


    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection()) {
            try ( var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to clear auth table: %s", e.getMessage()));
        }
    }
    
}
