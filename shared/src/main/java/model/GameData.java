package model;

import chess.ChessGame;
import com.google.gson.*;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    
    public String toString() {
        return new Gson().toJson(this);
    }

    public GameData updateWhite(String username) {
        return new GameData(this.gameID, username, this.blackUsername, this.gameName, this.game);
    }

    public GameData updateBlack(String username) {
        return new GameData(this.gameID, this.whiteUsername, username, this.gameName, this.game);
    }

    public GameData updateGame(ChessGame game) {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, game);
    }

}
