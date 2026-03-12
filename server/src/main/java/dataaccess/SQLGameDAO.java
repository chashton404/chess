package dataaccess;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import model.GameData;
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
            throw new DataAccessException(String.format("Unable to add new game: %s", e.getMessage()));
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
            throw new DataAccessException(String.format("Unable to list games: %s", e.getMessage()));
        }
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

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to clear game table: %s", e.getMessage()));
        }

    }
    
}
