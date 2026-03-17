package dataaccess;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import model.ListGameData;
import chess.ChessGame;

public class SQLGameDAO implements GameDAO {

    public Integer createGame(String gameName) throws DataAccessException {

        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";

        ChessGame newGame = new ChessGame();

        var gameJson = new Gson().toJson(newGame);

        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, gameName);
            preparedStatement.setString(4, gameJson);

            preparedStatement.executeUpdate();

            try (var resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to add new game: %s", e.getMessage()));
        }

        return null;
    }

    public Collection<ListGameData> listGames() throws DataAccessException {
        Collection<ListGameData> list = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";

        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)){
            try (var resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    ListGameData listItem = new ListGameData(resultSet.getInt("gameID"), resultSet.getString("whiteUsername"), 
                                                            resultSet.getString("blackUsername"), resultSet.getString("gameName"));
                    list.add(listItem);
                }
                return list;
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to list games: %s", e.getMessage()));
        }
    }

    public Boolean checkGame(Integer gameID) throws DataAccessException {
        var statement = "SELECT EXISTS(SELECT 1 FROM game WHERE gameID = ?)";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);

            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getBoolean(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Error checking game exsitence: %s", e.getMessage()));
        }
        return false;
    }

    public Boolean checkColor(Integer gameID, String playerColor) throws DataAccessException {
        
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("Error: color must be BLACK or WHITE");
        }
        
        var statement = "";
        String column = null;
       
        if (playerColor.equals("WHITE")){
            statement = "SELECT whiteUsername FROM game WHERE gameID = ?";
            column = "whiteUsername";
       } else if (playerColor.equals("BLACK")){
            statement = "SELECT blackUsername FROM game WHERE gameID = ?";
            column = "blackUsername";
       }

       try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)){
            preparedStatement.setInt(1, gameID);    

            try (var resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    String username = resultSet.getString(column);
                    return username == null;
                }
            }
       } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Error checking the color: %s", e.getMessage()));
       }
       return false;
    }

    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {
        String column = playerColor.equals("WHITE") ? "whiteUsername" : "blackUsername";
        var statement = "UPDATE game SET " + column + " = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Error updating game: %s", e.getMessage()));
        }
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: Unable to clear game table: %s", e.getMessage()));
        }

    }
    
}
